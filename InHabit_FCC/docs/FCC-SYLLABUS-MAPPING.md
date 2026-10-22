# InHabit FCC project — syllabus & viva mapping

This document maps the implementation to the uploaded Full Stack Development (F0002) course outline. It is a practical guide for evaluation; it does not claim that every optional lab exercise is part of the core workflow.

| Syllabus topic / outcome | Implementation in this project | Where to show it |
|---|---|---|
| HTML5, semantic structure | `client/index.html`; React renders semantic `main`, `header`, `nav`, `section`, `article`, `form`, and `footer` elements | Sign-in page, dashboard and forms in browser DevTools |
| CSS selectors, box model, Flexbox/Grid | `client/src/styles.css` | Sidebar + dashboard grid, stats cards, complaint cards and responsive lost/found grid |
| Responsive/mobile-first design | Media queries at `max-width: 1250px`, `920px`, and `650px` | Resize browser or use DevTools device toolbar |
| JavaScript form validation and state | `client/src/App.jsx` | Sign-in, registration, grievance modal, upload interaction, vote and status actions |
| React components/hooks | `AuthScreen`, `Sidebar`, `Header`, `StatCard`, `ComplaintCard`, `ComplaintBoard`, `LostFoundBoard`, `Modal`; hooks include `useState`, `useEffect`, `useMemo`, `useCallback` | `client/src/App.jsx` |
| Node.js + Express REST API | Express app and route modules | `server/src/server.js`, `server/src/routes/*.js`; test with Postman |
| HTTP/URL module | Express receives HTTP requests; server parses request URLs using Node's `URL` class | request audit middleware in `server/src/server.js` |
| Node file system + Events | `node:fs/promises` appends JSON Lines audit activity; `node:events` publishes audit events | `server/src/services/audit.js`, runtime `server/logs/audit.jsonl` (created on use) |
| File uploads | Multer accepts one JPG/PNG/WEBP image up to 5 MB | `POST /api/complaints/upload`; grievance/lost-and-found forms |
| Email automation | Optional Nodemailer script sends one configurable test email | `server/scripts/sendDemoEmail.js`; configure SMTP variables and run `npm run email-demo -w server` |
| MongoDB CRUD/modeling | Mongoose models: `User`, `UserProfile`, `Complaint`, `Vote`, `LostFound` | `server/src/models/` and CRUD routes |
| One-to-many relationship | A user can submit multiple complaints through `Complaint.createdBy` reference | `server/src/models/Complaint.js` |
| One-to-one-style data | `UserProfile.userId` is unique and the `User.profile` field references that profile document | `server/src/models/User.js` |
| Many-to-many relationship | `Vote` joins `User` and `Complaint` with references and a unique compound index | `server/src/models/Vote.js`; vote endpoint |
| Indexing + advanced aggregation | Compound/text indexes in models; `$group` aggregation for totals/category/status | models and `GET /api/dashboard/stats` |
| Authentication / role access | bcrypt password hashes, expiring JWT, role middleware | `server/src/routes/auth.routes.js`, `server/src/middleware/auth.js` |
| Postman API testing | Ready-to-import collection | `postman/InHabit-FCC.postman_collection.json` |
| Git / Jenkins | `Jenkinsfile` installs, runs tests and builds the client | Root `Jenkinsfile`; connect a Jenkins job to the GitHub repository |
| Render deployment | Blueprint defines API and static frontend services | Root `render.yaml`; configure `MONGODB_URI` and verify service URLs |

## Important stack choices to explain honestly

1. **FCC database is MongoDB, not MySQL.** The uploaded FSD syllabus explicitly calls for MongoDB CRUD, data modelling, relationships, aggregation/indexes and Atlas integration (Unit 4 / CO4). Keep the existing MySQL file for the separate Java/DBMS submission; it is not consumed by this FCC application.
2. **Tomcat is not this app's runtime.** The syllabus mentions Tomcat in the DevOps unit, but this project uses React/Vite for the client and Node/Express for the API. Tomcat is for Java servlet/JSP applications. Show the Jenkins/Git process for this project and complete any Tomcat-specific lab separately if the faculty explicitly requires it.
3. **MongoDB relationship modelling is document-oriented.** The separate `userprofiles` collection has a unique `userId` and the `User.profile` reference, demonstrating a one-to-one relationship. The `votes` collection bridges the many-to-many User–Complaint relationship. This is not a 1:1 translation of the 15-table SQL normalisation exercise.
4. **Uploads on local disk are educational/demo-grade.** A free hosted service may use ephemeral storage. For a real system, persist images in object storage and save the durable URL in MongoDB.
5. **Seed account data is for evaluation only.** Do not use the included demo credentials as real campus credentials, and do not commit `server/.env`.

## Suggested demo sequence (5–7 minutes)

1. Sign in as Student and show existing grievances and the category/status filters.
2. Create a grievance with a title, details, category, location, and optional image.
3. Sign in as a second student; support someone else's grievance and show the count increase. Try voting on one's own complaint or voting twice to demonstrate server-side validation.
4. Sign in as Admin; change a status, assign the technician, then open the dashboard analytics.
5. Sign in as Technician and update a ticket assigned to that account.
6. Open Lost & Found, publish a notice, and demonstrate the claim/mark-found workflow.
7. Open Postman and show auth, complaint CRUD, role-protected status/assignment, and `/api/dashboard/stats`; point to the MongoDB collections and aggregation pipeline if asked.
8. Close by showing responsive layout, environment variable usage, Jenkinsfile, and the Render blueprint.
