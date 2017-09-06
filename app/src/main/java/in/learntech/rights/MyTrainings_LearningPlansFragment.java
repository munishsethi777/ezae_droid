package in.learntech.rights;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import in.learntech.rights.utils.ImageViewCircleTransform;

/**
 * Created by munishsethi on 04/09/17.
 */
@SuppressLint("ValidFragment")
public class MyTrainings_LearningPlansFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.my_training_learningplans_fragment, container, false);
//        ImageView img1 = (ImageView) view.findViewById(R.id.image1);
//        ImageView img2 = (ImageView) view.findViewById(R.id.image2);
//        ImageView img3 = (ImageView) view.findViewById(R.id.image3);
//        String urlPost1 = BuildConfig.IMAGE_URL + "activity/style-1/Activity-1-img-1.jpg";
//        String urlPost2 = BuildConfig.IMAGE_URL + "activity/style-1/Activity-1-img-2.jpg";
//        String urlPost3 = BuildConfig.IMAGE_URL + "activity/style-3/Activity-3-img.jpg";

//        loadImageCircleRequest(img1, urlPost1);
//        loadImageCircleRequest(img2, urlPost2);
//        loadImageCircleRequest(img3, urlPost3);
        return view;
    }

    private void loadImageCircleRequest(ImageView img, String url){
        Glide.with(this)
                .load(url)
                .transform(new ImageViewCircleTransform(getActivity()))
                .into(img);
    }

    private void loadImageRequest(ImageView bg, String url) {
        Glide.with(this)
                .load(url)
                .thumbnail(0.01f)
                .centerCrop()
                .crossFade()
                .into(bg);
    }
}
