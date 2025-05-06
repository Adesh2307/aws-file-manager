# AWS File Manager

A modern web application for managing files using AWS S3, built with Spring Boot and React.

## Features

- Upload files to AWS S3
- List files with metadata
- Preview/download files using pre-signed URLs
- Delete files
- Responsive UI with Tailwind CSS

## Prerequisites

- Java 11 or higher
- Node.js 14 or higher
- AWS Account with S3 bucket
- AWS Access Key and Secret Key

## Setup

### Backend (Spring Boot)

1. Navigate to the backend directory:
```bash
cd backend
```

2. Update AWS credentials in `src/main/resources/application.yml`:
```yaml
aws:
  region: your-region
  s3:
    bucket: your-bucket-name
  credentials:
    access-key: your-access-key
    secret-key: your-secret-key
```

3. Run the Spring Boot application:
```bash
./mvnw spring-boot:run
```

The backend server will start at http://localhost:8080

### Frontend (React)

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

The frontend application will start at http://localhost:3000

## Usage

1. Open http://localhost:3000 in your browser
2. Use the file input to select files for upload
3. Click the "Upload" button to upload files to S3
4. View the list of uploaded files with their details
5. Use the "View" link to preview/download files
6. Click the trash icon to delete files

## Architecture

### Backend
- Spring Boot for REST API
- AWS SDK for S3 integration
- Pre-signed URLs for secure file access

### Frontend
- React for UI components
- Tailwind CSS for styling
- Axios for API calls
- Heroicons for icons

## API Endpoints

- `GET /api/files` - List all files
- `POST /api/files/upload` - Upload a file
- `DELETE /api/files/{fileName}` - Delete a file
- `GET /api/files/{fileName}/metadata` - Get file metadata

## Security

- CORS configured for frontend access
- Pre-signed URLs for secure file access
- AWS credentials managed securely

## License

MIT License
