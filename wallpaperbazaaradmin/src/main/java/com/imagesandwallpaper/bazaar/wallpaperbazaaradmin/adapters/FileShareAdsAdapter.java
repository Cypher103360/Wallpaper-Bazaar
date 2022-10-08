package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.R;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.UpdateAdsActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ProWallModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ProWallModelList;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.utils.CommonMethods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileShareAdsAdapter extends RecyclerView.Adapter<FileShareAdsAdapter.ViewHolder> {
    List<String> fileShareAdsList;
    Context context;
    Dialog imageDialog, loadingDialog;
    ImageView chooseImage;
    ApiInterface apiInterface;
    String fileShareUrl, fileShareId;

    public FileShareAdsAdapter(List<String> fileShareAdsList, Context context) {
        this.fileShareAdsList = fileShareAdsList;
        this.context = context;
        apiInterface = ApiWebServices.getApiInterface();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_share_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.fileShareItem.setText(fileShareAdsList.get(position));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateAdsActivity.class);

            if (position < 5) {
                intent.putExtra("key", fileShareAdsList.get(position));
                context.startActivity(intent);
            } else {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                builder.setTitle("Url or Ads ID");
                builder.setMessage("Change your url or ads ids");
                builder.setNegativeButton("URL", (dialog, which) -> {
                    fetchProFileShareUrl(fileShareAdsList.get(position));
                });
                builder.setPositiveButton("ADs Id", ((dialog, which) -> {
                    intent.putExtra("key", fileShareAdsList.get(position));
                    context.startActivity(intent);
                })).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return fileShareAdsList.size();
    }

    private void proFileShareUrlDialog() {
        loadingDialog = CommonMethods.loadingDialog(context);
        imageDialog = new Dialog(context);
        imageDialog.setContentView(R.layout.upload_image_layout);
        imageDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        imageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        imageDialog.setCancelable(false);
        imageDialog.show();

        Map<String, String> map = new HashMap<>();

        TextView dialogTitle = imageDialog.findViewById(R.id.dialog_title);
        EditText proUrlEditText = imageDialog.findViewById(R.id.img_quality);
        TextInputLayout textInputLayout = imageDialog.findViewById(R.id.textInputLayout);
        textInputLayout.setHint("File Transfer Url");
        proUrlEditText.setHint("Enter Pro Wallpaper Url");
        proUrlEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        chooseImage = imageDialog.findViewById(R.id.choose_imageView);
        Button cancelBtn = imageDialog.findViewById(R.id.cancel_btn);
        Button uploadImageBtn = imageDialog.findViewById(R.id.upload_image_btn);

        dialogTitle.setText("Update File Transfer Url");
        proUrlEditText.setText(fileShareUrl);
        cancelBtn.setOnClickListener(view -> imageDialog.dismiss());
        chooseImage.setVisibility(View.GONE);

        uploadImageBtn.setText("Upload Url");
        uploadImageBtn.setOnClickListener(view -> {
            loadingDialog.show();
            String proUrl = proUrlEditText.getText().toString().trim();

            if (TextUtils.isEmpty(proUrl)) {
                proUrlEditText.setError("Url Required");
                proUrlEditText.requestFocus();
                loadingDialog.dismiss();
            } else {
                map.put("id", fileShareId);
                map.put("url", proUrl);
                updateProWallUrl(map);
            }
        });
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileShareItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileShareItem = itemView.findViewById(R.id.file_Share_item);
        }
    }

    private void updateProWallUrl(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.updateFileShareUrlsById(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    fetchProFileShareUrl(map.get("id"));
                    loadingDialog.dismiss();
                    imageDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }

    private void fetchProFileShareUrl(String id) {
        Call<ProWallModel> call = apiInterface.fetchFileShareUrlById(id);
        call.enqueue(new Callback<ProWallModel>() {
            @Override
            public void onResponse(@NonNull Call<ProWallModel> call, @NonNull Response<ProWallModel> response) {

                assert response.body() != null;

                    fileShareId = response.body().getId();
                    fileShareUrl = response.body().getUrl();
                    Log.d("myData",fileShareId+ " " + fileShareUrl);
                    proFileShareUrlDialog();
            }

            @Override
            public void onFailure(@NonNull Call<ProWallModel> call, @NonNull Throwable t) {
                Log.d("ggggggggg", t.getMessage());
            }
        });
    }


}
