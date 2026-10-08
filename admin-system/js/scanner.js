let scanner;

async function startScanner(elementId) {
  scanner = new Html5Qrcode(elementId);
  const config = { fps: 10, qrbox: { width: 250, height: 250 } };

  await scanner.start(
    { facingMode: "environment" },
    config,
    async decodedText => {
      await scanner.pause(true);
      try {
        const sessionId = document.getElementById('sessionId').value.trim();
        if (!sessionId) throw new Error('Enter the active session ID first.');
        const result = await markAttendance(decodedText.trim(), sessionId);
        const student = result.student || {};
        document.getElementById('result').textContent =
          `${result.action}: ${student.full_name || decodedText} — ${result.message}`;
        document.getElementById('result').className = 'scan-result success';
      } catch (e) {
        document.getElementById('result').textContent = e.message;
        document.getElementById('result').className = 'scan-result error';
      } finally {
        setTimeout(() => scanner.resume(), 1800);
      }
    },
    () => {}
  );
}
