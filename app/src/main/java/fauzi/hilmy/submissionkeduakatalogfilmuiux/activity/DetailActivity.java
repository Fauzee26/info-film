package fauzi.hilmy.submissionkeduakatalogfilmuiux.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fauzi.hilmy.submissionkeduakatalogfilmuiux.R;
import fauzi.hilmy.submissionkeduakatalogfilmuiux.data.Detail;
import fauzi.hilmy.submissionkeduakatalogfilmuiux.db.DatabaseContract;
import fauzi.hilmy.submissionkeduakatalogfilmuiux.helper.Genre;
import fauzi.hilmy.submissionkeduakatalogfilmuiux.loader.DetailLoader;

import static android.provider.BaseColumns._ID;
import static fauzi.hilmy.submissionkeduakatalogfilmuiux.db.DatabaseContract.CONTENT_URI;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Detail>> {

    public static String EXTRA_RATING = "extra_rating";
    public static String EXTRA_NAME = "extra_name";
    public static String EXTRA_DESC = "extra_desc";
    public static String EXTRA_DATE = "extra_date";
    public static String EXTRA_POSTER = "extra_poster";
    public static String EXTRA_POSTER_BACK = "back";
    public static String EXTRA_ID = "id";
    public static String EXTRA_MOVIE_ID = "id_mov";

    public static String EXTRA_GENRE = "genre";

    String name, desc, poster, release, rate, poster_back, id_str, genre;
    @BindView(R.id.fabFavorite)
    FloatingActionButton fabFavorite;
    @BindView(R.id.filmNamee)
    TextView txtFilm_name;
    @BindView(R.id.datee)
    TextView txtDate;
    @BindView(R.id.filmoverviewwww)
    TextView txtDesc;
    @BindView(R.id.imgBackdrop)
    ImageView imgBackdrop;
    @BindView(R.id.imgPoster)
    ImageView imgPoster;
    @BindView(R.id.txtVote)
    TextView txtVote;
    @BindView(R.id.txtPopularity)
    TextView txtPopularity;
    @BindView(R.id.txtRuntime)
    TextView txtRuntime;
    @BindView(R.id.txtYear)
    TextView txtYear;
    @BindView(R.id.txtTagline)
    TextView txtTagline;
    int id_movie;
    long id;
    @BindView(R.id.txtGenre)
    TextView txtGenre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        fabFavorite.setImageResource(R.drawable.ic_fav_not);
        id_movie = getIntent().getIntExtra(EXTRA_ID, 0);
        name = getIntent().getStringExtra(EXTRA_NAME);
        desc = getIntent().getStringExtra(EXTRA_DESC);
        poster = getIntent().getStringExtra(EXTRA_POSTER);
        release = getIntent().getStringExtra(EXTRA_DATE);
        genre = getIntent().getStringExtra(EXTRA_GENRE);
        rate = getIntent().getStringExtra(EXTRA_RATING);
        poster_back = getIntent().getStringExtra(EXTRA_POSTER_BACK);
        id_str = String.valueOf(id_movie);
        Log.e("Id :", String.valueOf(id_movie));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(release);
            SimpleDateFormat newDateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy");
            String date_release = newDateFormat.format(date);
            txtDate.setText(date_release);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            Date date = dateFormat.parse(release);
            SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy");
            String date_release = newDateFormat.format(date);
            txtYear.setText(date_release);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtFilm_name.setText(name);
        txtDesc.setText(desc);
        txtGenre.setText(Genre.getGenres(genre, this));
        Picasso.with(DetailActivity.this)
                .load("http://image.tmdb.org/t/p/original" + poster)
                .into(imgPoster);

        Picasso.with(DetailActivity.this)
                .load("http://image.tmdb.org/t/p/original" + poster_back)
                .resize(2200, 1080)
                .centerCrop()
                .into(imgBackdrop);

        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MOVIE_ID, id_str);
        if (id_str != null) {
            getSupportLoaderManager().initLoader(0, bundle, DetailActivity.this);
        }
//        setFavorite();
    }

    @SuppressLint("SetTextI18n")
    private void init(Detail detail) {
        txtVote.setText(String.valueOf(detail.getVote()) + "/10");
        txtPopularity.setText(String.valueOf(detail.getVote_count()) + " voted");
        txtRuntime.setText(String.valueOf(detail.getRuntime()) + getString(R.string.minut));
        txtTagline.setText(detail.getTagline());
    }

    public boolean setFavorite() {
        Uri uri = Uri.parse(CONTENT_URI + "");
        boolean favorite = false;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        String getTitle;
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getLong(0);
                getTitle = cursor.getString(1);
                if (getTitle.equals(getIntent().getStringExtra(EXTRA_NAME))) {
                    fabFavorite.setImageResource(R.drawable.ic_fav_true);
                    favorite = true;
                }
            } while (cursor.moveToNext());

        }
        return favorite;
    }

    public void favorite() {
        if (setFavorite()) {
            Uri uri = Uri.parse(CONTENT_URI + "/" + id);
            getContentResolver().delete(uri, null, null);
            fabFavorite.setImageResource(R.drawable.ic_fav_not);
            toast(getString(R.string.deleted));
        } else {
            ContentValues values = new ContentValues();
            values.put(_ID, id_str);
            values.put(DatabaseContract.MovieColumns.MOVIE_TITLE, name);
            values.put(DatabaseContract.MovieColumns.MOVIE_POSTER, poster);
            values.put(DatabaseContract.MovieColumns.MOVIE_DATE, release);
            values.put(DatabaseContract.MovieColumns.MOVIE_DESCRIPTION, Genre.getGenres(genre, this));
            values.put(DatabaseContract.MovieColumns.MOVIE_RATING, rate);
            values.put(DatabaseContract.MovieColumns.MOVIE_POSTER_BACK, poster_back);

            getContentResolver().insert(CONTENT_URI, values);
            setResult(101);

            fabFavorite.setImageResource(R.drawable.ic_fav_true);
            toast(getString(R.string.added));
        }
    }

    @OnClick(R.id.fabFavorite)
    public void onViewClicked() {
        favorite();
    }

    private void toast(String message) {
        Toast.makeText(this, name + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<ArrayList<Detail>> onCreateLoader(int id, Bundle args) {
        String mQuery = "";
        if (args != null) {
            mQuery = args.getString(EXTRA_MOVIE_ID);
        }

        return new DetailLoader(this, mQuery);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Detail>> loader, ArrayList<Detail> data) {
        init(data.get(0));
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Detail>> loader) {

    }
}
