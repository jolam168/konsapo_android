package com.hasuka.konsapo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.CookieManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


import java.util.*;


public class HomeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
//    private int[] tabTitles = {R.string.tab_home,R.string.tab_search,R.string.tab_new,R.string.tab_bookmark,R.string.tab_message};
    private int[] tabTitles = {R.string.tab_home,R.string.tab_search,R.string.tab_new};

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private CustomTabLayout tabLayout;
    private List<String> urls = new ArrayList<String>();
    private int currentPage = 0;

    private int[] tabIcons = {
            R.drawable.ic_action_home,
            R.drawable.ic_action_search,
            R.drawable.ic_action_new
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Fabric.with(this, new Crashlytics());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        urls.add("https://konkatsu10.com/");
        urls.add("https://konkatsu10.com/index.php?app_controller=search&type=mid&run=true&category=&category_PAL[]=match+or#search");
        urls.add("https://konkatsu10.com/index.php?app_controller=search&type=news&run=true&category=news&category_PAL[]=match%20comp");

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),urls);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        tabLayout = (CustomTabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("debug","Tab Selected");
                currentPage = tab.getPosition();

                PlaceholderFragment f = (PlaceholderFragment)mSectionsPagerAdapter.getRegisteredFragment(tab.getPosition());
                f.webView.reload();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d("debug","Tab UnSelected");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d("debug","Tab ReSelected");
                PlaceholderFragment f = (PlaceholderFragment)mSectionsPagerAdapter.getRegisteredFragment(tab.getPosition());
                f.webView.loadUrl(urls.get(tab.getPosition()));

            }
        });
        setupTabIcons();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptFileSchemeCookies(true);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String cookiesValue = "";
        sharedPref.getString("cookies",cookiesValue);
        if (cookiesValue.length()>0){
            cookieManager.setCookie("konkatsu10.com",cookiesValue);
        }else{
            cookieManager.getCookie("konkatsu10.com");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCookies();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveCookies();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveCookies();
    }

    private void setupTabIcons() {

        for(int i = 0; i < 3;i++){
            LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tabContent = (TextView) tabLinearLayout.findViewById(R.id.tabContent);
            tabContent.setText("  "+getApplicationContext().getResources().getString(tabTitles[i]));
            tabContent.setCompoundDrawablesWithIntrinsicBounds(tabIcons[i],0, 0, 0);
            tabLayout.getTabAt(i).setCustomView(tabContent);

        }



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    PlaceholderFragment f = (PlaceholderFragment)mSectionsPagerAdapter.getRegisteredFragment(currentPage);
                    if (f.webView.canGoBack()) {
                        f.webView.goBack();
                    } else {
                        if (currentPage>0){
                            mViewPager.setCurrentItem(0);
                        }else {
                            finish();
                        }
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_URL = "section_url";

        private WebView webView;
        private String mUrl;
        private View rootView;
        private ProgressBar progressBar;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(String sectionUrl) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_URL, sectionUrl);
            fragment.setArguments(args);

            return fragment;
        }


        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            if (webView != null) {
                webView.destroy();
            }

            progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_photo_page_progress_bar);
            progressBar.setMax(100);

            mUrl = getArguments().getString(ARG_SECTION_URL);



            webView = (WebView) rootView.findViewById(R.id.webview);
            webView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return true;
                }
            });
            webView.setLongClickable(false);
//            webView.setOnTouchListener(new View.OnTouchListener() {
//
//                public boolean onTouch(View v, MotionEvent event) {
//                    return (event.getAction() == MotionEvent.ACTION_MOVE);
//                }
//            });

            WebSettings webSettings = webView.getSettings();
            webSettings.setSupportMultipleWindows(true);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setSupportZoom(false);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setUseWideViewPort(false);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            webView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 100) {
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress);
                    }
                }

                @Override
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

                    ((WebView.WebViewTransport) resultMsg.obj).setWebView(new WebView(view.getContext()));
                    resultMsg.sendToTarget();

                    return true;
                }
            });

            webView.setWebViewClient(new WebViewClient(){


                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    saveCookies();


                    return false;
                }

//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    CookieManager cookieManager = CookieManager.getInstance();
//                    String cookies = cookieManager.getCookie(url);
//                    SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = settings.edit();
//                    editor.putString("cookies", cookies);
//                    editor.commit();
//                    if (url.startsWith("newtab:")) {
//
//                        Log.d("debug","newtab");
//                    }else{
//                        view.loadUrl(url);
//                    }
//                    return true;
//                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {


                    super.onPageStarted(view, url, favicon);

                }

                @Override
                public void onPageFinished(WebView view, String url) {
//                    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//                        view.loadUrl("javascript: var allLinks = document.getElementsByTagName('a'); if (allLinks) {var i;for (i=0; i<allLinks.length; i++) {var link = allLinks[i];var target = link.getAttribute('target'); if (target && target == '_blank') {link.setAttribute('target','_self');link.href = 'newtab:'+link.href;}}}");
//                    }
                    if (android.os.Build.VERSION.SDK_INT >= 21) CookieManager.getInstance().flush();
                    else CookieSyncManager.getInstance().sync();
                }

                @Override
                public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                    handler.proceed(getString(R.string.USERNAME), getString(R.string.PASSWORD));
                    super.onReceivedHttpAuthRequest(view, handler, host, realm);
                }
            });

            webView.setHorizontalScrollBarEnabled(false);
            webView.loadUrl(mUrl);


            return rootView;


        }



        private void loadCookies(){
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            CookieManager cookieManager = CookieManager.getInstance();
            String cookiesValue = "";
            sharedPref.getString("cookies",cookiesValue);
            if (cookiesValue.length()>0){
                cookieManager.setCookie(webView.getUrl(),cookiesValue);
            }
        }

        private void saveCookies(){
            CookieManager cookieManager = CookieManager.getInstance();
            String cookies = cookieManager.getCookie("konkatsu10.com");
            SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.putString("cookies",cookies);
            editor.apply();
        }



    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<String> mDatas;
        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public SectionsPagerAdapter(FragmentManager fm, List<String> datas) {
            super(fm);
            this.mDatas = datas;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            String url = mDatas.get(position);
            return PlaceholderFragment.newInstance(url);
        }

        @Override
        public int getCount() {

            return tabIcons.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return null;
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


    }


    private void loadCookies(){
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptFileSchemeCookies(true);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String cookiesValue = "";
        sharedPref.getString("cookies",cookiesValue);
        if (cookiesValue.length()>0){
            cookieManager.setCookie("konkatsu10.com",cookiesValue);
        }else{
            cookieManager.getCookie("konkatsu10.com");
        }
    }

    private void saveCookies(){
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie("konkatsu10.com");
        SharedPreferences settings = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.putString("cookies",cookies);
        editor.apply();
    }
}
