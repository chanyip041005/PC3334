/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package GoogleDrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import java.io.ByteArrayOutputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;

public class GoogleDriveSync {

    private final String APPLICATION_NAME = "PasswordManager";

    private String uploadFilePath;
    private final String DIR_FOR_DOWNLOADS = "data"; //for testing
    private java.io.File uploadFile;

    private final java.io.File DATA_STORE_DIR = new java.io.File("./data/");

    private FileDataStoreFactory dataStoreFactory;

    /**
     * Global instance of the HTTP transport.
     */
    private HttpTransport httpTransport;

    /**
     * Global instance of the JSON factory.
     */
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private Drive drive;

    public GoogleDriveSync(String filePath) {
        uploadFilePath = filePath;
        uploadFile = new java.io.File(uploadFilePath);

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            // authorization
            Credential credential = Authorize();
            // set up the global Drive instance
            drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
                    APPLICATION_NAME).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void Run() {
        try {
            // run commands
            System.out.println("Starting Resumable Media Upload");
            File uploadedFile = this.InsertFile();

            String fileID = uploadedFile.getId();


            /*
            //System.out.println("Updating Uploaded File Name");
            File updatedFile = updateFileWithTestSuffix(uploadedFile.getId());
            System.out.println("Starting Resumable Media Download");
             */
            //downloadFile(updatedFile);
            this.DownloadFile(fileID);

            System.out.println("Success!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return;
    }

    /**
     * Authorizes the installed application to access user's protected data.
     */
    private Credential Authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(GoogleDriveSync.class
                        .getResourceAsStream("/client_id.json")));

        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(DriveScopes.DRIVE_FILE)).setDataStoreFactory(dataStoreFactory)
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    /**
     * Uploads a file using either resumable or direct media upload.
     */
    public File InsertFile() {
        try {
            File fileMetadata = new File();
            fileMetadata.setTitle(uploadFile.getName());

            ////local file
           // FileContent fileContent = new FileContent("text/plain", uploadFile);

            //Drive.Files.Insert insert = drive.files().insert(fileMetadata, fileContent);
            Drive.Files.Insert insert = drive.files().insert(fileMetadata);
            MediaHttpUploader uploader = insert.getMediaHttpUploader();
            //uploader.setDirectUploadEnabled(useDirectUpload);
            uploader.setProgressListener(new FileUploadProgressListener());
            return insert.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public File GetUploadedFile(String fileID) {
        File file = null;

        try {
            file = drive.files().get(fileID).execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return file;
    }

    /**
     * Downloads a file using either resumable or direct media download.
     */
    public boolean DownloadFile(String fileID) {
        try {
            File file = drive.files().get(fileID).execute();

            // create parent directory (if necessary)
            java.io.File parentDir = new java.io.File(DIR_FOR_DOWNLOADS);
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                //throw new IOException("Unable to create parent directory");
            }

            OutputStream out = new FileOutputStream(new java.io.File(parentDir, file.getTitle()));

            MediaHttpDownloader downloader
                    = new MediaHttpDownloader(httpTransport, drive.getRequestFactory().getInitializer());
            //downloader.setDirectDownloadEnabled(useDirectDownload);
            downloader.setProgressListener(new FileDownloadProgressListener());
            downloader.download(new GenericUrl(file.getDownloadUrl()), out);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public File UpdateFile(String fileID) {
        try {
            // First retrieve the file from the API.
            File file = drive.files().get(fileID).execute();

            //local file
            FileContent fileContent = new FileContent("text/plain", uploadFile);

            // Send the request to the API.
            File updatedFile = drive.files().update(fileID, file, fileContent).execute();

            return updatedFile;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
