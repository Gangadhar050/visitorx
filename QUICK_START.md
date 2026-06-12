# Quick Start Guide - Photo Upload Feature

## 1. Build the Project
```bash
cd "D:\CounterX\visitor-x-backend (1)\visitor-x-backend"
./mvnw.cmd clean package -DskipTests
```

## 2. Start the Application
```bash
# Option A: Run JAR file
java -jar target/visitor-x-backend-0.0.1-SNAPSHOT.jar

# Option B: Run via Maven
./mvnw.cmd spring-boot:run
```

The server will start on `http://localhost:8080`

## 3. Test the Photo Upload

### Option 1: Use Built-in Demo Page
Open in your browser:
```
http://localhost:8080/photo-upload-demo.html
```

Features:
- 📷 Capture photo with camera
- 📁 Upload photo from file
- 👁️ Preview before upload
- Form validation
- Displays response

### Option 2: Use cURL
```bash
curl -X POST http://localhost:8080/api/visitor/register-with-photo \
  -F "name=John Doe" \
  -F "mobileNumber=9876543210" \
  -F "email=john@gmail.com" \
  -F "address=123 Main St" \
  -F "purposeOfVisit=INTERVIEW" \
  -F "photo=@C:\path\to\photo.jpg"
```

### Option 3: Use Postman
1. Import the project or create new request
2. Method: `POST`
3. URL: `http://localhost:8080/api/visitor/register-with-photo`
4. Tab: **Body** → Select **form-data**
5. Add fields:
   | Key | Value | Type |
   |-----|-------|------|
   | name | John Doe | Text |
   | mobileNumber | 9876543210 | Text |
   | email | john@gmail.com | Text |
   | address | 123 Main St | Text |
   | purposeOfVisit | INTERVIEW | Text |
   | photo | [Select File] | File |
6. Click **Send**

## 4. Frontend Integration

### JavaScript (Vanilla)
```html
<form id="visitorForm">
  <input type="file" id="photoInput" accept="image/*" required>
  <input type="text" id="name" required>
  <input type="tel" id="mobileNumber" pattern="[0-9]{10}" required>
  <input type="email" id="email" pattern=".*@gmail\.com" required>
  <button type="submit">Register</button>
</form>

<script>
document.getElementById('visitorForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  
  const formData = new FormData();
  formData.append('name', document.getElementById('name').value);
  formData.append('mobileNumber', document.getElementById('mobileNumber').value);
  formData.append('email', document.getElementById('email').value);
  formData.append('photo', document.getElementById('photoInput').files[0]);
  
  const response = await fetch('http://localhost:8080/api/visitor/register-with-photo', {
    method: 'POST',
    body: formData
  });
  
  if (response.ok) {
    const data = await response.json();
    console.log('Success:', data);
    // Display photo: <img src={data.photoBase64} />
  }
});
</script>
```

### React
```jsx
import { useRef, useState } from 'react';

function VisitorForm() {
  const fileInputRef = useRef(null);
  const [formData, setFormData] = useState({});

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const fd = new FormData();
    fd.append('name', formData.name);
    fd.append('mobileNumber', formData.mobileNumber);
    fd.append('email', formData.email);
    fd.append('photo', fileInputRef.current.files[0]);

    const response = await fetch('http://localhost:8080/api/visitor/register-with-photo', {
      method: 'POST',
      body: fd
    });

    if (response.ok) {
      const result = await response.json();
      console.log('Registered:', result);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="text" onChange={(e) => setFormData({...formData, name: e.target.value})} />
      <input type="tel" onChange={(e) => setFormData({...formData, mobileNumber: e.target.value})} />
      <input type="email" onChange={(e) => setFormData({...formData, email: e.target.value})} />
      <input type="file" ref={fileInputRef} accept="image/*" />
      <button type="submit">Register</button>
    </form>
  );
}
```

## 5. API Response Format

```json
{
  "visitorId": 1,
  "name": "John Doe",
  "mobileNumber": "9876543210",
  "email": "john@gmail.com",
  "address": "123 Main St",
  "purposeOfVisit": "INTERVIEW",
  "photoBase64": "data:image/jpeg;base64,/9j/4AAQSkZJRg...",
  "visitDateTime": "2026-06-11T21:45:00"
}
```

Display photo in HTML/React:
```html
<img src="data:image/jpeg;base64,/9j/4AAQSkZJRg..." alt="Visitor Photo" />
```

## 6. Key Information

**Endpoints:**
- `POST /api/visitor/register-with-photo` - Register with photo (NEW)
- `POST /api/visitor/register` - Register without photo (still works)
- `GET /api/visitor/{id}` - Get visitor details with photo

**Supported Image Formats:**
- JPEG, PNG, GIF, WebP, BMP

**Limits:**
- Max file size: 5MB
- Photo stored as: JPG (binary in database)

**Database:**
- Automatic migration from photoUrl → photo column
- No manual SQL needed

## 7. Troubleshooting

**Q: Camera permission denied?**
- Check browser settings
- Try another browser
- Use file upload instead

**Q: CORS error?**
- Check if frontend URL is in CORS config
- See CorsConfig.java to add your domain

**Q: Photo too large?**
- Max size is 5MB
- Compress image before upload

**Q: Photo not displaying?**
- Ensure photoBase64 starts with "data:image/jpeg;base64,"
- Check browser console for errors

**Q: Database errors?**
- Ensure MySQL is running
- Check disk space
- Verify user permissions

## 8. Documentation Files

Located in project root:
- `IMPLEMENTATION_SUMMARY.md` - Complete implementation details
- `PHOTO_UPLOAD_GUIDE.md` - Detailed frontend examples
- `photo-upload-demo.html` - Interactive demo page

## Next Steps

1. ✅ Application is built and ready
2. ✅ Start the server
3. ✅ Open demo page: `http://localhost:8080/photo-upload-demo.html`
4. ✅ Test with camera or file upload
5. ✅ Check response in browser console
6. ✅ Integrate with your frontend

---

**Happy Photo Uploading! 📸**

