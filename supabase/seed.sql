--After creating a user in Supabase Authentication,
--replace the UUID below with that user's Auth user ID.

Example:
update public.profiles
set role = 'SUPER_ADMIN'
where id = 'AUTH-USER-UUID';

Or insert:
insert into public.profiles(id, full_name, role)
values ('AUTH-USER-UUID', 'System Owner', 'SUPER_ADMIN');

Create a first attendance session:
insert into public.attendance_sessions
(session_name, attendance_date, start_time, end_time, active)
values ('Morning Session', current_date, '08:00', '12:00', true);

Sample students:
insert into public.students(student_number, full_name, course, batch, qr_value)
values
('SC001', 'John Perera', 'BSc Science', '2026', 'SC001'),
('SC002', 'Anne Silva', 'BSc Science', '2026', 'SC002');
