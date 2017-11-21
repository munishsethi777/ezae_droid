package in.learntech.rights;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tubb.smrv.SwipeHorizontalMenuLayout;
import com.tubb.smrv.SwipeMenuLayout;
import com.tubb.smrv.listener.SwipeFractionListener;

import java.util.ArrayList;
import java.util.List;

import in.learntech.rights.BusinessObjects.CompanyUser;
import in.learntech.rights.messages.MessageAdapter;
import in.learntech.rights.messages.MessageClickListener;
import in.learntech.rights.messages.MessageModel;
import in.learntech.rights.utils.ImageViewCircleTransform;
import in.learntech.rights.utils.StringConstants;

/**
 * Created by baljeetgaheer on 01/11/17.
 */

public class CompanyUserAdapter extends RecyclerView.Adapter<CompanyUserAdapter.ItemViewHolder> {
    private LayoutInflater mInflater;
    private Context context;
    private MessageClickListener clickListener = null;
    private static ArrayList<CompanyUser> dataList;
    private ViewGroup mParent;
    public CompanyUserAdapter(Context ctx, ArrayList<CompanyUser> data) {
        context = ctx;
        dataList = data;
        mInflater = LayoutInflater.from(context);
    }
    @Override
    public CompanyUserAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mParent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_user_row, parent, false);
        CompanyUserAdapter.ItemViewHolder itemViewHolder = new CompanyUserAdapter.ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(CompanyUserAdapter.ItemViewHolder holder, int position) {
        String imageDirName = "UserImages/";
        String type = "User";
        if(!dataList.get(position).getType().equals("user")){
            imageDirName = "AdminImages/";
            type = "Admin";
        }

            String imageName = dataList.get(position).getImageName();
            String imageUrl = StringConstants.IMAGE_URL + imageDirName + imageName;
            if(imageName != null && !imageName.equals("null") && !imageName.equals("")) {
                Glide.with(context)
                        .load(imageUrl)
                        .transform(new ImageViewCircleTransform(context))
                        .into(holder.imgProfile);
            }else{
                Glide.with(context)
                        .load(R.drawable.dummy)
                        .transform(new ImageViewCircleTransform(context))
                        .into(holder.imgProfile);
            }

        boolean swipeEnable = true;
        holder.sml.setSwipeEnable(swipeEnable);
        holder.textName.setText(type +": "+dataList.get(position).getFullName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setClickListener(MessageClickListener listener) {
        this.clickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgProfile;
        private SwipeHorizontalMenuLayout sml;
        private ImageView buttonDelete;
        private LinearLayout itemContainer;
        private LinearLayout colorMask;
        private TextView textName,message,timeMesage;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imgProfile = (ImageView) itemView.findViewById(R.id.imgProfile);
            sml = (SwipeHorizontalMenuLayout) itemView.findViewById(R.id.sml);
           // buttonDelete = (ImageView) itemView.findViewById(R.id.buttonDelete);
            itemContainer = (LinearLayout) itemView.findViewById(R.id.itemContainer);
            colorMask = (LinearLayout) itemView.findViewById(R.id.colorMask);
            textName = (TextView) itemView.findViewById(R.id.textName);
            //message = (TextView) itemView.findViewById(R.id.message);
           // timeMesage = (TextView) itemView.findViewById(R.id.timeMesage);
            colorMask.setAlpha(0.0f);
            //buttonDelete.setOnClickListener(this);
            imgProfile.setOnClickListener(this);
            itemContainer.setOnClickListener(this);

            sml.setSwipeFractionListener(new SwipeFractionListener() {
                @Override
                public void beginMenuSwipeFraction(SwipeMenuLayout swipeMenuLayout, float fraction) {
                }

                @Override
                public void endMenuSwipeFraction(SwipeMenuLayout swipeMenuLayout, float fraction) {
                    colorMask.setAlpha(fraction);
                }
            });

        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.buttonDelete) {
                onItemDismiss(getAdapterPosition());
            }else if(v.getId() == R.id.check){
                clickListener.itemClicked(v,getAdapterPosition());
            }else if (clickListener != null) {
                clickListener.itemClicked(v, getAdapterPosition());
            }
        }

    }

    private void onItemDismiss(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    public void replaceAll(ArrayList<CompanyUser> models) {
        dataList = models;
        notifyDataSetChanged();
    }
}
