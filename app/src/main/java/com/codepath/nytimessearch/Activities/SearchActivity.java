package com.codepath.nytimessearch.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.nytimessearch.Article;
import com.codepath.nytimessearch.EndlessRecyclerViewScrollListener;
import com.codepath.nytimessearch.ItemClickSupport;
import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.SearchFilters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    ArrayList<Article> articles;
    ArticleAdapter adapter;
    RecyclerView rvResults;
    SearchView searchView;
    SearchFilters filters;
    private final int REQUEST_CODE = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpViews();
    }

    private void setUpViews() {
        filters = new SearchFilters();
        rvResults = (RecyclerView) findViewById(R.id.rvResults);
        articles = new ArrayList<>();
        adapter = new ArticleAdapter(this, articles);
        rvResults.setAdapter(adapter);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvResults.setLayoutManager(gridLayoutManager);

        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
            }
        });
        ItemClickSupport.addTo(rvResults).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // do it
                        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                        //get the article to display
                        Article article = articles.get(position);
                        //pass in that article into intent
                        i.putExtra("article", Parcels.wrap(article));
                        //launch the activity
                        startActivity(i);
                    }
                }
        );

    }

    public void customLoadMoreDataFromApi(int page) {
        final int currentSize = adapter.getItemCount();
        String query = searchView.getQuery().toString();

        //make a network call
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
        //specify parameters
        RequestParams params = new RequestParams();
        params.put("api-key", "0a2c4a8301b841cca2fb51e04775a6ef");
        params.put("page", page);
        params.put("q", query);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    List<Article> moreArticles = Article.fromJSONArray(articleJsonResults);
                    articles.addAll(moreArticles);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery(query);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void searchQuery(String query) {
        //make a network call
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        //specify parameters
        RequestParams params = new RequestParams();
        params.put("api-key", "0a2c4a8301b841cca2fb51e04775a6ef");
        params.put("page", 0);
        params.put("q", query);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;
                articles.clear();

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    //direct change to adapter, changes arraylist
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    //Log.d("DEBUG", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSettingsClick(MenuItem item) {
        Intent i = new Intent(SearchActivity.this, FilterActivity.class);
        i.putExtra("filters", Parcels.wrap(filters));
        startActivityForResult(i, REQUEST_CODE);
    }

    //once return from filter activity.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            filters = Parcels.unwrap(data.getParcelableExtra("filters")); //get + update the filters
            if (filters.isUpdated()) {
                if (searchView.getQuery() != null) {
                    searchQueryFilters(filters);
                }
            }
            //if query isn't null {
            //then run // searchQueryFilters(filters); //run search query }
        }
    }

    //to call with filters. only if filters.boolean != false
    public void searchQueryFilters(SearchFilters filters) {
        //make a network call
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        String query = searchView.getQuery().toString();
        //specify parameters
        RequestParams params = new RequestParams();
        params.put("api-key", "0a2c4a8301b841cca2fb51e04775a6ef");
        params.put("page", 0);
        params.put("q", query);

        //add news desk
        ArrayList<String> news = filters.getNewsDesks();
        for (int i = 0; i < news.size(); i++) {
            Log.d("search", news.toString());
            params.put("news_desk", news.get(i));
        }

        //add sort order
        if (!filters.getSort().equalsIgnoreCase("None")) {
            Log.d("search", filters.getSort());
            params.put("sort", filters.getSort());
        }

        //add begin date
        if (filters.getBeginDate() != 0) {

            params.put("begin_date", filters.getBeginDate());
            Log.d("search", filters.getBeginDate() + "");
        }

        //add end date
        if (filters.getEndDate() != 0) {
            params.put("end_date", filters.getEndDate());
            Log.d("search", filters.getEndDate() + "");
        }

        //go through all the new filters here

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TESTING", response.toString());
                JSONArray articleJsonResults = null;
                articles.clear();

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    //direct change to adapter, changes arraylist
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    Log.d("TESTING", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
