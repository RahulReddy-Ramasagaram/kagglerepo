package com.java.kaggle_project.blob;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

public class AzureBlobInsert {
    public void insertFileIntoAzureBlob(String storage_connection_string, String file_path,String
            container_reference) throws URISyntaxException, InvalidKeyException, StorageException, IOException {

        // Connect to azure storage account.
        CloudStorageAccount account = CloudStorageAccount.parse(storage_connection_string);
        CloudBlobClient serviceClient = account.createCloudBlobClient();
        File file=new File(file_path);

        // file should be uploaded into specified container.

        CloudBlobContainer container = serviceClient.getContainerReference(container_reference);
        container.createIfNotExists();
        CloudBlockBlob blob = container.getBlockBlobReference(file.getName());
        // Upload file to blob.
        blob.uploadFromFile(file.getAbsolutePath());
    }
}