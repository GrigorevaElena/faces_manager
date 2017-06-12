package grigoreva.facesmanager.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import grigoreva.facesmanager.R;
import grigoreva.facesmanager.model.PersonViewModel;

/**
 * Created by админ2 on 04.04.2017.
 */
public class FaceListAdapter extends RecyclerView.Adapter <ViewHolder>{

    @NonNull
    private List<PersonViewModel> mData = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_face_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PersonViewModel person = mData.get(position);
        ViewHolder vh = (ViewHolder) holder;
        vh.mName.setText(person.getName());
        vh.mIsContact.setText(person.getIsContact() ? "Из списка контактов" : "Из приложения");
        if (person.getBitmap() != null) {
            vh.mPhoto.setImageBitmap(person.getBitmap());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @NonNull
    public List<PersonViewModel> getData() {
        return mData;
    }

    public void setData(List<PersonViewModel> mData) {
        this.mData = mData != null ? mData : new ArrayList<PersonViewModel>();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mAvgColor;
        ImageView mPhoto;
        TextView mIsContact;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name);
            mAvgColor = (TextView) itemView.findViewById(R.id.color);
            mPhoto = (ImageView) itemView.findViewById(R.id.main_photo);
            mIsContact = (TextView) itemView.findViewById(R.id.status);
        }
    }
}
