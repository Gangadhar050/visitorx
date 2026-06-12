# Photo Upload Flow Diagram

## Complete Photo Upload Process

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           FRONTEND (Browser)                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  USER INTERFACE                                                           │
│  ┌──────────────┐                                                         │
│  │ Camera Input │  OR  ┌──────────────────────┐                          │
│  │   (webcam)   │       │  File Input (Browse) │                         │
│  └──────┬───────┘       └──────────┬───────────┘                         │
│         │                          │                                      │
│         └──────────────┬───────────┘                                      │
│                        │                                                  │
│                  [Image Blob]                                             │
│                        │                                                  │
│    Form Data Input (multipart/form-data)                                 │
│    ┌─────────────────────────────────────────────────────────┐           │
│    │ • name: "John Doe"        (Text)                        │           │
│    │ • mobileNumber: "9876543210"  (Text)                    │           │
│    │ • email: "john@gmail.com"  (Text)                       │           │
│    │ • photo: [Image Blob]      (File)  ◄── JPG/PNG/GIF    │           │
│    └─────────────────────────────────────────────────────────┘           │
│                        │                                                  │
│            HTTP POST multipart/form-data                                 │
│                        │                                                  │
│                        ▼                                                  │
└─────────────────────────────────────────────────────────────────────────┘
                         │
                         │
          ┌──────────────▼────────────────┐
          │   Network (HTTP Request)      │
          │   Port: 8080                  │
          │   Endpoint: /api/visitor/     │
          │            register-with-photo│
          └──────────────┬────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      BACKEND (Spring Boot Server)                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  VisitorController.registerVisitorWithPhoto()                            │
│  ┌─────────────────────────────────────────────────────────────┐        │
│  │ 1. Receive multipart form data                              │        │
│  │    • Extract form fields                                    │        │
│  │    • Extract photo file (MultipartFile)                    │        │
│  │    • Convert to VisitorRequestDTO                          │        │
│  └─────────────────────────────────────────────────────────────┘        │
│                        │                                                 │
│                        ▼                                                 │
│  VisitorService.registerVisitorWithPhoto()                              │
│  ┌─────────────────────────────────────────────────────────────┐        │
│  │ 2. Validation                                               │        │
│  │    ✓ Email format (Gmail only)                             │        │
│  │    ✓ Mobile number format (10 digits)                      │        │
│  │    ✓ Photo required & not empty                            │        │
│  │    ✓ Check for duplicate email/phone                       │        │
│  └─────────────────────────────────────────────────────────────┘        │
│                        │                                                 │
│                        ▼                                                 │
│  PhotoService.convertToJpg(MultipartFile)                               │
│  ┌─────────────────────────────────────────────────────────────┐        │
│  │ 3. Image Processing                                         │        │
│  │                                                              │        │
│  │    Input Image (Any Format)                                │        │
│  │    ├─ PNG (with transparency) ──┐                          │        │
│  │    ├─ GIF                       ├─→ Java ImageIO Reader   │        │
│  │    ├─ WebP                      ├─→ Load as BufferedImage │        │
│  │    ├─ BMP                       ├─→ Validate image         │        │
│  │    └─ JPEG                      ┘
│  │                                                              │        │
│  │    Convert to RGB (JPG compatible)                          │        │
│  │    └─→ Create new BufferedImage(TYPE_INT_RGB)             │        │
│  │        Draw original image on new RGB image                │        │
│  │                                                              │        │
│  │    Encode to JPG (Quality: 95%)                            │        │
│  │    └─→ ImageIO.write(image, "jpg", outputStream)          │        │
│  │                                                              │        │
│  │    Output: byte[] (JPG binary data)                        │        │
│  └─────────────────────────────────────────────────────────────┘        │
│                        │                                                 │
│                        ▼ [byte[] - JPG Data]                             │
│                                                                           │
│  Create Visitor Entity                                                   │
│  ┌─────────────────────────────────────────────────────────────┐        │
│  │ Visitor.builder()                                           │        │
│  │   .name("John Doe")                                         │        │
│  │   .mobileNumber("9876543210")                               │        │
│  │   .email("john@gmail.com")                                  │        │
│  │   .address("123 Main St")                                   │        │
│  │   .purposeOfVisit(PurposeOfVisit.INTERVIEW)               │        │
│  │   .photo(jpgByteArray)  ◄── Binary JPG data               │        │
│  │   .visitDateTime(now)                                       │        │
│  │   .build()                                                  │        │
│  └─────────────────────────────────────────────────────────────┘        │
│                        │                                                 │
│                        ▼                                                 │
│  VisitorRepository.save(visitor)                                         │
│  ┌─────────────────────────────────────────────────────────────┐        │
│  │ 4. Persist to Database                                      │        │
│  │                                                              │        │
│  │    SQL: INSERT INTO visitors (...)                         │        │
│  │         VALUES (..., photo_blob, ...)                      │        │
│  │                                                              │        │
│  │    Where photo_blob = byte[] (JPG binary)                  │        │
│  └─────────────────────────────────────────────────────────────┘        │
│                        │                                                 │
│                        ▼                                                 │
│  Convert Response                                                        │
│  ┌─────────────────────────────────────────────────────────────┐        │
│  │ 5. Prepare API Response                                     │        │
│  │                                                              │        │
│  │    Get photo byte[] from database                           │        │
│  │    ↓                                                         │        │
│  │    Base64.encode(photoBytes)                               │        │
│  │    ↓                                                         │        │
│  │    prepend: "data:image/jpeg;base64,"                      │        │
│  │    ↓                                                         │        │
│  │    VisitorResponseDTO.photoBase64 =                         │        │
│  │    "data:image/jpeg;base64,/9j/4AAQSkZJRg..."             │        │
│  └─────────────────────────────────────────────────────────────┘        │
│                        │                                                 │
│                        ▼                                                 │
│  Return JSON Response                                                    │
│  ┌─────────────────────────────────────────────────────────────┐        │
│  │ {                                                            │        │
│  │   "visitorId": 1,                                           │        │
│  │   "name": "John Doe",                                       │        │
│  │   "mobileNumber": "9876543210",                             │        │
│  │   "email": "john@gmail.com",                                │        │
│  │   "address": "123 Main St",                                 │        │
│  │   "purposeOfVisit": "INTERVIEW",                            │        │
│  │   "photoBase64": "data:image/jpeg;base64,/9j/4AAQSk...",  │        │
│  │   "visitDateTime": "2026-06-11T21:45:00"                    │        │
│  │ }                                                            │        │
│  └─────────────────────────────────────────────────────────────┘        │
│                        │                                                 │
│            HTTP 201 (Created) Response                                   │
│                        │                                                 │
└────────────────────────┼──────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      FRONTEND (Browser) - Response                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  6. Display Result                                                       │
│                                                                           │
│    Parse JSON Response                                                   │
│    ├─ Extract photoBase64                                               │
│    ├─ Extract other fields                                              │
│    └─ Success! Visitor registered                                       │
│                                                                           │
│    Display Photo                                                         │
│    ┌──────────────────────────────────┐                                 │
│    │ <img src="{photoBase64}" />      │                                 │
│    │  Shows JPG in browser            │                                 │
│    └──────────────────────────────────┘                                 │
│                                                                           │
│    Display Visitor Details                                              │
│    ┌────────────────────────────────────────────┐                       │
│    │ Name: John Doe                             │                       │
│    │ Mobile: 9876543210                         │                       │
│    │ Email: john@gmail.com                      │                       │
│    │ Address: 123 Main St                       │                       │
│    │ Purpose: Interview                         │                       │
│    │ DateTime: 2026-06-11 21:45:00             │                       │
│    └────────────────────────────────────────────┘                       │
│                                                                           │
└─────────────────────────────────────────────────────────────────────────┘
```

## Database Schema

```
┌──────────────────────────────────────────────────────┐
│ visitors (MySQL Table)                                │
├──────────────────────────────────────────────────────┤
│ visitor_id        │ BIGINT PRIMARY KEY              │
│ name              │ VARCHAR(100) NOT NULL           │
│ mobile_number     │ VARCHAR(15) NOT NULL UNIQUE     │
│ email             │ VARCHAR(100) NOT NULL UNIQUE    │
│ address           │ VARCHAR(255)                    │
│ purpose_of_visit  │ ENUM(...)                       │
│ photo             │ LONGBLOB NOT NULL ◄─ JPG Data  │
│ visit_date_time   │ DATETIME (Auto-timestamp)       │
│ created_at        │ DATETIME (Auto-created)         │
└──────────────────────────────────────────────────────┘
```

## Photo Storage & Retrieval

```
Storage Flow:
─────────────

Input Photo (PNG, GIF, etc.)
        ↓
   [ImageIO Read]
        ↓
   BufferedImage
        ↓
   Convert to RGB (JPG compatible)
        ↓
   ImageIO.write(..., "jpg", ...)
        ↓
   byte[] (JPG binary)
        ↓
   MySQL LONGBLOB Column
        ↓
   Compressed storage (~70-80% reduction)


Retrieval Flow:
───────────────

MySQL LONGBLOB → byte[] (JPG binary)
        ↓
Base64.encode(byte[])
        ↓
"data:image/jpeg;base64,/9j/4AAQSkZJ..."
        ↓
<img src="{base64}" /> in HTML
        ↓
Browser decodes & displays JPG
```

## Image Format Conversion

```
Supported Input Formats:           Output Format:
─────────────────────────────      ──────────────

PNG (with transparency)     ┐
GIF (animated/static)       ├─→    JPG (Quality 95%)
WebP                        │      Binary data
BMP                         │      Max 4GB per image
JPEG/JPG                    ┘
```

## Error Handling Flow

```
┌─ Photo Upload Request
│
├─ Validation Failed?
│  ├─ Photo is null/empty ────────────→ "Photo is required"
│  ├─ Invalid MIME type ──────────────→ "Invalid image format"
│  ├─ File > 5MB ─────────────────────→ "File size exceeds limit"
│  ├─ Email format invalid ──────────→ "Only Gmail allowed"
│  ├─ Mobile format invalid ─────────→ "Must be 10 digits"
│  └─ Duplicate email/phone ────────→ "Already registered"
│
├─ Image Processing Failed?
│  ├─ Corrupted image ───────────────→ "Unable to read image"
│  ├─ IOException ───────────────────→ "Failed to process image"
│  └─ Unsupported format ───────────→ "Invalid image file"
│
├─ Database Error?
│  ├─ Connection failed ─────────────→ DB Connection Error
│  ├─ BLOB too large ───────────────→ Data Too Large Error
│  └─ Constraint violation ────────→ Duplicate/Not Null Error
│
└─ Success ────────────────────────→ 201 Created + Response JSON
```

## Configuration Summary

```
Application Configuration:
─────────────────────────

Property                          │ Value
──────────────────────────────────┼─────────────────────
spring.servlet.multipart.       │
  max-file-size                 │ 10MB (configurable)
──────────────────────────────────┼─────────────────────
spring.servlet.multipart.       │
  max-request-size              │ 10MB (configurable)
──────────────────────────────────┼─────────────────────
PhotoService Max File Size       │ 5MB (code level)
──────────────────────────────────┼─────────────────────
JPG Quality                       │ 95% (optimized)
──────────────────────────────────┼─────────────────────
Supported Image MIME Types       │ image/jpeg, image/png,
                                  │ image/gif, image/webp,
                                  │ image/bmp
──────────────────────────────────┼─────────────────────
Database Column Type             │ LONGBLOB (Max 4GB)
──────────────────────────────────┼─────────────────────
CORS Enabled                      │ Yes (see CorsConfig)
──────────────────────────────────┼─────────────────────
```

## API Endpoint Summary

```
POST /api/visitor/register-with-photo
────────────────────────────────────

Headers:
  Content-Type: multipart/form-data

Body (Form Data):
  name (Text):              string, required
  mobileNumber (Text):      string, 10 digits, required
  email (Text):             string, @gmail.com, required
  address (Text):           string, optional
  purposeOfVisit (Text):    INTERVIEW | INTERNSHIP | 
                            FULL_TIME_EMPLOYEENT | 
                            BUSINESS_MEETING, optional
  photo (File):             image file, required

Response (201 Created):
  {
    "visitorId": number,
    "name": string,
    "mobileNumber": string,
    "email": string,
    "address": string,
    "purposeOfVisit": string,
    "photoBase64": "data:image/jpeg;base64,..."
    "visitDateTime": ISO 8601 timestamp
  }

Error Response (400/409):
  {
    "timestamp": ISO 8601,
    "status": number,
    "error": string,
    "message": string,
    "path": string
  }
```

---

This diagram shows the complete lifecycle of a photo upload from clicking the button in the browser to displaying the saved photo in the response.

