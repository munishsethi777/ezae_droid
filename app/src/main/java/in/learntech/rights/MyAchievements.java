package in.learntech.rights;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import in.learntech.rights.utils.ImageViewCircleTransform;

public class MyAchievements extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_achievements);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView img1 = (ImageView) findViewById(R.id.image1);
        ImageView img2 = (ImageView) findViewById(R.id.image2);
        ImageView img3 = (ImageView) findViewById(R.id.image3);
        String urlPost1 = BuildConfig.IMAGE_URL + "activity/style-1/Activity-1-img-1.jpg";
        String urlPost2 = BuildConfig.IMAGE_URL + "activity/style-1/Activity-1-img-2.jpg";
        String urlPost3 = BuildConfig.IMAGE_URL + "activity/style-1/Activity-3-img-3.jpg";

        loadImageCircleRequest(img1, urlPost1);
        loadImageCircleRequest(img2, urlPost2);
        loadImageCircleRequest(img3, urlPost3);


    }

    private void loadImageCircleRequest(ImageView img, String url) {
        Glide.with(this)
                .load(url)
                .transform(new ImageViewCircleTransform(this))
                .into(img);
    }
}