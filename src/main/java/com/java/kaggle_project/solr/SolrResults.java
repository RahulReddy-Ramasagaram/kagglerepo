package com.java.kaggle_project.solr;

import com.java.kaggle_project.common.KaggleCommon;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.noggit.JSONUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SolrResults {
    public List<String> getResultsFromSolr(String solr_url, String search_word){
        AddDataSolr addDataSolr=new AddDataSolr();
        List<String> arrayListSolr = null;
        try {
            // Connect to apache solr cleint.

            HttpSolrClient solr = addDataSolr.connectToSolr(solr_url);
            SolrQuery query = new SolrQuery();
            String solr_search_query = "dataset_name:" + search_word + "* OR category:" + search_word + "*";

            // Query the solr doc.

            query.setQuery(solr_search_query).setStart(0).setRows(1000);
            QueryResponse response = solr.query(query);

            // Get the response.

            SolrDocumentList solrDocumentList = response.getResults();
            String returnValue = JSONUtil.toJSON(solrDocumentList);
            JSONArray jsonArray = new JSONArray(returnValue);

            // List of dataset names in array list.
            arrayListSolr = IntStream.range(0, jsonArray.length())
                    .mapToObj(index -> ((JSONObject) jsonArray.get(index)).optString(KaggleCommon.DATASET_NAME))
                    .toList();
        }
        catch(Exception e){
            //
        }
        return arrayListSolr;
    }
}