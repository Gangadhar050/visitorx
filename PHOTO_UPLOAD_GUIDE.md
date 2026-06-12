# Photo Upload Feature Documentation

## Overview
This document describes the photo upload functionality for the Visitor X Backend. Photos are now captured through the frontend, converted to JPG format on the server, and stored as binary data in the database.

## Database Changes
- **Old**: `photoUrl` (VARCHAR) - stored URL path
- **New**: `photo` (LONGBLOB) - stores actual image binary data in JPG format

The database migration is automatic via Hibernate DDL `update` mode.

## Backend Endpoints

### 1. Register Visitor with Photo (Recommended)
**Endpoint**: `POST /api/visitor/register-with-photo`

**Content-Type**: `multipart/form-data`

**Parameters**:
- `name` (String, required) - Visitor name
- `mobileNumber` (String, required) - 10-digit mobile number
- `email` (String, required) - Gmail address
- `address` (String, optional) - Visitor address
- `purposeOfVisit` (String, optional) - One of: INTERVIEW, INTERNSHIP, FULL_TIME_EMPLOYEENT, BUSINESS_MEETING
- `photo` (File, required) - Image file (JPG, PNG, GIF, WebP, BMP)

**Maximum File Size**: 5MB

**Response**:
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

### 2. Get Visitor by ID
**Endpoint**: `GET /api/visitor/{id}`

**Response**: Same structure as above, includes photo in Base64 format

## Frontend Implementation

### JavaScript Example - Camera Capture

```html
<!-- HTML -->
<div>
  <video id="preview" width="320" height="240"></video>
  <button onclick="capturePhoto()">Capture Photo</button>
  <canvas id="canvas" style="display:none;"></canvas>
  <img id="preview-img" style="display:none;">
</div>

<form id="visitorForm">
  <input type="text" id="name" placeholder="Name" required>
  <input type="tel" id="mobileNumber" placeholder="Mobile (10 digits)" required>
  <input type="email" id="email" placeholder="Gmail" required>
  <input type="text" id="address" placeholder="Address">
  <select id="purposeOfVisit">
    <option value="">Select Purpose</option>
    <option value="INTERVIEW">Interview</option>
    <option value="INTERNSHIP">Internship</option>
    <option value="FULL_TIME_EMPLOYEENT">Full-time Employment</option>
    <option value="BUSINESS_MEETING">Business Meeting</option>
  </select>
  <input type="file" id="photoInput" accept="image/*" style="display:none;">
  <button type="submit">Register Visitor</button>
</form>
```

```javascript
// JavaScript
let photoBlob = null;

// Option 1: Camera Capture
async function capturePhoto() {
  const video = document.getElementById('preview');
  const canvas = document.getElementById('canvas');
  const ctx = canvas.getContext('2d');
  
  // Get access to camera
  if (!video.srcObject) {
    const stream = await navigator.mediaDevices.getUserMedia({ 
      video: { facingMode: 'user' } 
    });
    video.srcObject = stream;
  }
  
  ctx.drawImage(video, 0, 0);
  
  // Convert canvas to blob
  canvas.toBlob((blob) => {
    photoBlob = blob;
    
    // Show preview
    const previewImg = document.getElementById('preview-img');
    previewImg.src = URL.createObjectURL(blob);
    previewImg.style.display = 'block';
    
    // Hide video
    video.style.display = 'none';
  }, 'image/jpeg', 0.95);
}

// Option 2: File input (click to browse)
document.getElementById('photoInput').addEventListener('change', (e) => {
  photoBlob = e.target.files[0];
  
  // Show preview
  const previewImg = document.getElementById('preview-img');
  previewImg.src = URL.createObjectURL(photoBlob);
  previewImg.style.display = 'block';
});

// Handle form submission
document.getElementById('visitorForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  
  if (!photoBlob) {
    alert('Please capture a photo');
    return;
  }
  
  const formData = new FormData();
  formData.append('name', document.getElementById('name').value);
  formData.append('mobileNumber', document.getElementById('mobileNumber').value);
  formData.append('email', document.getElementById('email').value);
  formData.append('address', document.getElementById('address').value);
  formData.append('purposeOfVisit', document.getElementById('purposeOfVisit').value);
  formData.append('photo', photoBlob, 'photo.jpg');
  
  try {
    const response = await fetch('http://localhost:8080/api/visitor/register-with-photo', {
      method: 'POST',
      body: formData
    });
    
    if (response.ok) {
      const data = await response.json();
      alert('Visitor registered successfully!');
      console.log('Response:', data);
      
      // Display photo
      if (data.photoBase64) {
        const img = new Image();
        img.src = data.photoBase64;
        document.body.appendChild(img);
      }
    } else {
      alert('Error registering visitor');
    }
  } catch (error) {
    console.error('Error:', error);
    alert('Error: ' + error.message);
  }
});

// Allow clicking file input
document.getElementById('preview-img').addEventListener('click', () => {
  document.getElementById('photoInput').click();
});
```

### React Example

```jsx
import React, { useRef, useState } from 'react';

function VisitorForm() {
  const videoRef = useRef(null);
  const canvasRef = useRef(null);
  const [photoBlob, setPhotoBlob] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    mobileNumber: '',
    email: '',
    address: '',
    purposeOfVisit: ''
  });

  const startCamera = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ 
        video: { facingMode: 'user' } 
      });
      videoRef.current.srcObject = stream;
    } catch (error) {
      alert('Camera access denied: ' + error.message);
    }
  };

  const capturePhoto = () => {
    const canvas = canvasRef.current;
    const video = videoRef.current;
    const ctx = canvas.getContext('2d');
    
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
    
    canvas.toBlob((blob) => {
      setPhotoBlob(blob);
      setPreviewUrl(URL.createObjectURL(blob));
    }, 'image/jpeg', 0.95);
  };

  const handlePhotoSelect = (e) => {
    const file = e.target.files[0];
    if (file) {
      setPhotoBlob(file);
      setPreviewUrl(URL.createObjectURL(file));
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!photoBlob) {
      alert('Please capture a photo');
      return;
    }

    const fd = new FormData();
    fd.append('name', formData.name);
    fd.append('mobileNumber', formData.mobileNumber);
    fd.append('email', formData.email);
    fd.append('address', formData.address);
    fd.append('purposeOfVisit', formData.purposeOfVisit);
    fd.append('photo', photoBlob, 'photo.jpg');

    try {
      const response = await fetch('http://localhost:8080/api/visitor/register-with-photo', {
        method: 'POST',
        body: fd
      });

      if (response.ok) {
        const result = await response.json();
        alert('Visitor registered successfully!');
        console.log('Result:', result);
      } else {
        alert('Error registering visitor');
      }
    } catch (error) {
      console.error('Error:', error);
      alert('Error: ' + error.message);
    }
  };

  return (
    <div style={{ maxWidth: '500px', margin: '0 auto', padding: '20px' }}>
      <h1>Register Visitor</h1>
      
      <div style={{ marginBottom: '20px' }}>
        <h3>Capture Photo</h3>
        <button onClick={startCamera} type="button">Start Camera</button>
        <video 
          ref={videoRef} 
          width="320" 
          height="240" 
          style={{ display: 'block', marginTop: '10px' }}
          autoPlay 
        />
        <canvas 
          ref={canvasRef} 
          width="320" 
          height="240" 
          style={{ display: 'none' }}
        />
        <button onClick={capturePhoto} type="button" style={{ marginTop: '10px' }}>
          Capture Photo
        </button>
        <input 
          type="file" 
          accept="image/*" 
          onChange={handlePhotoSelect} 
          style={{ marginLeft: '10px' }}
        />
      </div>

      {previewUrl && (
        <div style={{ marginBottom: '20px' }}>
          <img src={previewUrl} alt="Preview" width="200" />
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="name"
          placeholder="Name"
          value={formData.name}
          onChange={handleInputChange}
          required
          style={{ width: '100%', marginBottom: '10px', padding: '8px' }}
        />
        <input
          type="tel"
          name="mobileNumber"
          placeholder="Mobile (10 digits)"
          value={formData.mobileNumber}
          onChange={handleInputChange}
          pattern="[0-9]{10}"
          required
          style={{ width: '100%', marginBottom: '10px', padding: '8px' }}
        />
        <input
          type="email"
          name="email"
          placeholder="Gmail"
          value={formData.email}
          onChange={handleInputChange}
          pattern="[A-Za-z0-9._%+-]+@gmail\.com"
          required
          style={{ width: '100%', marginBottom: '10px', padding: '8px' }}
        />
        <input
          type="text"
          name="address"
          placeholder="Address"
          value={formData.address}
          onChange={handleInputChange}
          style={{ width: '100%', marginBottom: '10px', padding: '8px' }}
        />
        <select
          name="purposeOfVisit"
          value={formData.purposeOfVisit}
          onChange={handleInputChange}
          style={{ width: '100%', marginBottom: '10px', padding: '8px' }}
        >
          <option value="">Select Purpose</option>
          <option value="INTERVIEW">Interview</option>
          <option value="INTERNSHIP">Internship</option>
          <option value="FULL_TIME_EMPLOYEENT">Full-time Employment</option>
          <option value="BUSINESS_MEETING">Business Meeting</option>
        </select>
        <button type="submit" style={{ width: '100%', padding: '10px' }}>
          Register Visitor
        </button>
      </form>
    </div>
  );
}

export default VisitorForm;
```

## Key Features

1. **Automatic JPG Conversion**: Any image format (PNG, GIF, WebP, BMP) is automatically converted to JPG
2. **Binary Storage**: Photos are stored as binary data in the database, not as file paths
3. **Base64 Response**: When retrieving visitor data, photos are returned as Base64 strings for easy frontend display
4. **File Size Limit**: Maximum 5MB per image
5. **Validation**: File type and size validation on the server side

## Error Handling

The API returns appropriate error messages:

```json
{
  "timestamp": "2026-06-11T21:45:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Photo is required"
}
```

## Testing the API

### Using cURL
```bash
curl -X POST http://localhost:8080/api/visitor/register-with-photo \
  -F "name=John Doe" \
  -F "mobileNumber=9876543210" \
  -F "email=john@gmail.com" \
  -F "address=123 Main St" \
  -F "purposeOfVisit=INTERVIEW" \
  -F "photo=@/path/to/photo.jpg"
```

### Using Postman
1. Set request type to POST
2. URL: `http://localhost:8080/api/visitor/register-with-photo`
3. Go to Body tab → Select "form-data"
4. Add fields:
   - name (Text): John Doe
   - mobileNumber (Text): 9876543210
   - email (Text): john@gmail.com
   - address (Text): 123 Main St
   - purposeOfVisit (Text): INTERVIEW
   - photo (File): Select your image file

## Database Migration

The system automatically updates the database schema from the old `photoUrl` column to the new `photo` BLOB column. No manual migration needed.

## Performance Considerations

- Photos are stored in `LONGBLOB` which can handle files up to 4GB
- Consider implementing image archival strategy for old photos
- Use indexing on frequently queried fields like email and mobileNumber

## Security Notes

1. **File Upload Validation**: Only image MIME types are accepted
2. **Max File Size**: Limited to 5MB to prevent DoS attacks
3. **Database Security**: Store sensitive data with proper encryption
4. **CORS Configuration**: Configure CORS appropriately for frontend domain

## Configuration

In `application.properties`:
```properties
# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

These can be adjusted based on your requirements.

