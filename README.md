# Tech G - Spring Boot landing page

## Run it
1. Install Java 17+, Maven and MySQL.
2. Open `src/main/resources/application.properties` and set:
   - `spring.datasource.password` (your MySQL password)
   - `spring.mail.username` / `spring.mail.password` (Gmail App Password)
   - `app.admin-email` (where enquiry emails go)
   - `app.admin.username` / `app.admin.password` (admin login)
3. `mvn spring-boot:run`
4. Website: http://localhost:8080   Admin: http://localhost:8080/admin/dashboard

The database and tables are created automatically, with starter products/services.

## Images (put your Tech G asset files here: src/main/resources/static/images/)
- hero.png                      (hero background photo)
- products/laptop.png, desktop.png, keyboard-mouse.png,
  printer.png, vga-cable.png, lan-cable.png
Missing images show a neutral placeholder. Products added in the admin panel
can point to any path, e.g. /images/products/monitor.png

## Admin login & image uploads
- Admin login page: /admin/login (username/password from application.properties). Use the "Log out" button in the sidebar.
- Product images are uploaded to Cloudinary from the product form. Fill in the three `cloudinary.*` values first.
