import 'dotenv/config';
import nodemailer from 'nodemailer';

// Optional syllabus demo: configure SMTP_* values in server/.env before running this script.
const required = ['SMTP_HOST', 'SMTP_USER', 'SMTP_PASS', 'SMTP_TO'];
const missing = required.filter((key) => !process.env[key]);
if (missing.length) {
  console.error(`Email demo not configured. Add ${missing.join(', ')} to server/.env before running this script.`);
  process.exit(1);
}

const transporter = nodemailer.createTransport({
  host: process.env.SMTP_HOST,
  port: Number(process.env.SMTP_PORT || 587),
  secure: Number(process.env.SMTP_PORT || 587) === 465,
  auth: { user: process.env.SMTP_USER, pass: process.env.SMTP_PASS }
});

try {
  const result = await transporter.sendMail({
    from: process.env.SMTP_FROM || process.env.SMTP_USER,
    to: process.env.SMTP_TO,
    subject: 'InHabit — Full Stack Development email demo',
    text: 'This is a test notification from the InHabit FCC mini project. No action is required.',
    html: '<main style="font-family:Arial,sans-serif;color:#203b2d"><h2>InHabit campus notification</h2><p>This is a test email from the Full Stack Development mini project.</p><p>No action is required.</p></main>'
  });
  console.log('Email demo sent. Message ID:', result.messageId);
} catch (error) {
  console.error('Email demo failed:', error.message);
  process.exitCode = 1;
}
