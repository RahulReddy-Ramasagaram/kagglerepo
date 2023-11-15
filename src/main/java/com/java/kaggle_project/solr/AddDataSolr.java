package com.java.kaggle_project.solr;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;

import java.util.Map;

public class AddDataSolr {

    public HttpSolrClient connectToSolr(String solr_url){

        // Connect to solr client.
        HttpSolrClient solr = null;
        try {
            solr = new HttpSolrClient.Builder(solr_url).build();
            solr.setParser(new XMLResponseParser());
        }
        catch(Exception e){
            //
        }
        return solr;
    }
    public void addDataToSolr(String solr_url, Map<String,String> userMap){

        // Add the dataset fields to solr doc.

        try {
            HttpSolrClient solr = connectToSolr(solr_url);
            SolrInputDocument dataset_doc = new SolrInputDocument();
            dataset_doc.addField("dataset_name", userMap.get("dataset_name"));
            dataset_doc.addField("category", userMap.get("category"));
            dataset_doc.addField("url", userMap.get("url"));
            dataset_doc.addField("dataset_location", userMap.get("dataset_location"));
            solr.add(dataset_doc);
            solr.commit();
        }
        catch(Exception e){
            //
        }
    }
}