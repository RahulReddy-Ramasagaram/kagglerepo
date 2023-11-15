package com.java.kaggle_project.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.java.kaggle_project.blob.AzureBlobInsert;
import com.java.kaggle_project.common.KaggleCommon;
import com.java.kaggle_project.db.InsertDatasetInfo;
import com.java.kaggle_project.object.Kaggle;
import com.java.kaggle_project.object.User;
import com.java.kaggle_project.repository.UserRepository;
import com.java.kaggle_project.solr.AddDataSolr;
import com.java.kaggle_project.solr.SolrResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {

    @Autowired
    private UserRepository userRepo;

    // Opening HTML page
    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    // Sign up form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_form";
    }

    // For user registration.
    @PostMapping("/process_register")
    public String processRegister(User user) {


        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        try {
            userRepo.save(user);
            return "register_success";
        }
        catch(Exception e){
            return "email_duplicated";
        }
    }

    // After logging in showing Kaggle Homepage
    @GetMapping("/kaggle")
    public String listUsers(Model model) {
        return "kaggle";
    }

    // Method to insert a dataset
    @PostMapping("/save")
    public ModelAndView save(@ModelAttribute Kaggle kaggle) {

        ModelAndView modelAndView = new ModelAndView();
        AzureBlobInsert azureBlobInsert=new AzureBlobInsert();
        InsertDatasetInfo insertDatasetInfo=new InsertDatasetInfo();
        AddDataSolr addDataSolr=new AddDataSolr();


        // Azure blob storage connection string
        String storage_connection_string
                = "DefaultEndpointsProtocol=https;"
                + "AccountName="+ System.getenv(KaggleCommon.ACCOUNT_NAME)
                + ";AccountKey="+System.getenv(KaggleCommon.ACCOUNT_KEY);
        String file_path="src/main/resources/datasets/"+kaggle.getDataset_location();
        String container_reference=System.getenv(KaggleCommon.CONTAINER_REFERENCE);

        // Azure sql database connection string
        String connection_url =
                "jdbc:sqlserver://"+System.getenv(KaggleCommon.AZURE_DATABASE_SERVER)+
                        ".database.windows.net:1433;database="+System.getenv(KaggleCommon.AZURE_DATABASE)+
                        ";encrypt=true;trustServerCertificate=false;";
        Map<String,String> userMap=new HashMap<>();
        String azure_username = System.getenv(KaggleCommon.AZURE_USERNAME);
        String azure_password = System.getenv(KaggleCommon.AZURE_PASSWORD);
        String solr_url = System.getenv(KaggleCommon.SOLR_URL);
        userMap.put(KaggleCommon.DATASET_NAME,kaggle.getDatasetName());
        userMap.put(KaggleCommon.URL,kaggle.getUrl());
        userMap.put(KaggleCommon.CATEGORY,kaggle.getCategory());
        userMap.put(KaggleCommon.DATASET_LOCATION,kaggle.getDataset_location());

        // File Upload to Azure Blob Storage
        try{
            azureBlobInsert.insertFileIntoAzureBlob(storage_connection_string,file_path,container_reference);
            insertDatasetInfo.insertDatasetInfoIntoAzure(connection_url,azure_username,azure_password,
                    userMap);
            addDataSolr.addDataToSolr(solr_url,userMap);
            modelAndView.setViewName(KaggleCommon.INSER_DATASET_HTML);
        }

        catch(Exception e){
            //
        }

        return modelAndView;

    }

    // Method to search datasets using solr

    @PostMapping("/searchd")
    public ModelAndView searchd(String search_word) {
        ModelAndView modelAndView = new ModelAndView();
        try{
            SolrResults solrResults = new SolrResults();
            modelAndView.setViewName(KaggleCommon.DATASET_INFO_HTML);
            String solr_url = System.getenv(KaggleCommon.SOLR_URL);
            List<String> arrayListSolr = solrResults.getResultsFromSolr(solr_url, search_word);
            modelAndView.addObject("arrayListSolr", arrayListSolr);
        }
        catch(Exception e){
            //
        }
        return modelAndView;
    }
}