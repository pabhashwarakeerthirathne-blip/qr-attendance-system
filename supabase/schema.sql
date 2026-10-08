-- SMARTQR ATTENDANCE DATABASE
-- Run this in Supabase SQL Editor.

create extension if not exists pgcrypto;

create table if not exists public.profiles (
    id uuid primary key references auth.users(id) on delete cascade,
    full_name text not null,
    role text not null check (role in ('ADMIN','SUPER_ADMIN')),
    active boolean not null default true,
    created_at timestamptz not null default now()
);

create table if not exists public.students (
    id uuid primary key default gen_random_uuid(),
    student_number text not null unique,
    full_name text not null,
    course text,
    batch text,
    qr_value text not null unique,
    active boolean not null default true,
    created_at timestamptz not null default now()
);

create table if not exists public.attendance_sessions (
    id uuid primary key default gen_random_uuid(),
    session_name text not null,
    attendance_date date not null,
    start_time time,
    end_time time,
    active boolean not null default true,
    created_at timestamptz not null default now()
);

create table if not exists public.attendance (
    id uuid primary key default gen_random_uuid(),
    student_id uuid not null references public.students(id) on delete restrict,
    session_id uuid not null references public.attendance_sessions(id) on delete cascade,
    in_time timestamptz,
    out_time timestamptz,
    scanned_by uuid references public.profiles(id) on delete set null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint attendance_times_valid check (
        out_time is null or in_time is null or out_time >= in_time
    )
);

create unique index if not exists one_open_attendance_per_student_session
on public.attendance(student_id, session_id)
where out_time is null;

create table if not exists public.audit_logs (
    id uuid primary key default gen_random_uuid(),
    user_id uuid references public.profiles(id) on delete set null,
    action text not null,
    target_table text,
    target_id uuid,
    details jsonb,
    created_at timestamptz not null default now()
);

create index if not exists students_number_idx on public.students(student_number);
create index if not exists attendance_session_idx on public.attendance(session_id);
create index if not exists attendance_student_idx on public.attendance(student_id);
create index if not exists attendance_created_idx on public.attendance(created_at desc);

create or replace function public.set_updated_at()
returns trigger
language plpgsql
as $$
begin
    new.updated_at = now();
    return new;
end;
$$;

drop trigger if exists attendance_updated_at on public.attendance;
create trigger attendance_updated_at
before update on public.attendance
for each row execute function public.set_updated_at();

-- Role helper.
-- SECURITY DEFINER avoids recursive RLS checks when checking profiles.
create or replace function public.current_user_role()
returns text
language sql
stable
security definer
set search_path = public
as $$
    select role from public.profiles
    where id = auth.uid() and active = true
    limit 1;
$$;

alter table public.profiles enable row level security;
alter table public.students enable row level security;
alter table public.attendance_sessions enable row level security;
alter table public.attendance enable row level security;
alter table public.audit_logs enable row level security;

-- Profiles
drop policy if exists profiles_self_read on public.profiles;
create policy profiles_self_read
on public.profiles for select
to authenticated
using (id = auth.uid() or public.current_user_role() = 'SUPER_ADMIN');

drop policy if exists profiles_super_admin_write on public.profiles;
create policy profiles_super_admin_write
on public.profiles for all
to authenticated
using (public.current_user_role() = 'SUPER_ADMIN')
with check (public.current_user_role() = 'SUPER_ADMIN');

-- Students
drop policy if exists students_admin_read on public.students;
create policy students_admin_read
on public.students for select
to authenticated
using (public.current_user_role() in ('ADMIN','SUPER_ADMIN'));

drop policy if exists students_super_admin_insert on public.students;
create policy students_super_admin_insert
on public.students for insert
to authenticated
with check (public.current_user_role() = 'SUPER_ADMIN');

drop policy if exists students_super_admin_update on public.students;
create policy students_super_admin_update
on public.students for update
to authenticated
using (public.current_user_role() = 'SUPER_ADMIN')
with check (public.current_user_role() = 'SUPER_ADMIN');

drop policy if exists students_super_admin_delete on public.students;
create policy students_super_admin_delete
on public.students for delete
to authenticated
using (public.current_user_role() = 'SUPER_ADMIN');

-- Sessions
drop policy if exists sessions_read on public.attendance_sessions;
create policy sessions_read
on public.attendance_sessions for select
to authenticated
using (public.current_user_role() in ('ADMIN','SUPER_ADMIN'));

drop policy if exists sessions_super_admin_write on public.attendance_sessions;
create policy sessions_super_admin_write
on public.attendance_sessions for all
to authenticated
using (public.current_user_role() = 'SUPER_ADMIN')
with check (public.current_user_role() = 'SUPER_ADMIN');

-- Attendance
drop policy if exists attendance_read on public.attendance;
create policy attendance_read
on public.attendance for select
to authenticated
using (public.current_user_role() in ('ADMIN','SUPER_ADMIN'));

drop policy if exists attendance_admin_insert on public.attendance;
create policy attendance_admin_insert
on public.attendance for insert
to authenticated
with check (public.current_user_role() in ('ADMIN','SUPER_ADMIN'));

drop policy if exists attendance_admin_update on public.attendance;
create policy attendance_admin_update
on public.attendance for update
to authenticated
using (public.current_user_role() in ('ADMIN','SUPER_ADMIN'))
with check (public.current_user_role() in ('ADMIN','SUPER_ADMIN'));

drop policy if exists attendance_super_delete on public.attendance;
create policy attendance_super_delete
on public.attendance for delete
to authenticated
using (public.current_user_role() = 'SUPER_ADMIN');

-- Audit logs
drop policy if exists audit_super_read on public.audit_logs;
create policy audit_super_read
on public.audit_logs for select
to authenticated
using (public.current_user_role() = 'SUPER_ADMIN');

drop policy if exists audit_authenticated_insert on public.audit_logs;
create policy audit_authenticated_insert
on public.audit_logs for insert
to authenticated
with check (user_id = auth.uid());
