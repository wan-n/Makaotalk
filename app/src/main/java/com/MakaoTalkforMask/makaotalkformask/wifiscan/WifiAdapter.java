package com.MakaoTalkforMask.makaotalkformask.wifiscan;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.MakaoTalkforMask.makaotalkformask.R;

import java.util.List;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.WifiViewHolder> {

    private List<ScanResult> items;
    private Context wContext;

    public WifiAdapter(List<ScanResult> items){
        this.items=items;
    }

    @NonNull
    @Override
    public WifiAdapter.WifiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        wContext = parent.getContext();

        return new WifiViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WifiAdapter.WifiViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ScanResult> list) {
        items.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class WifiViewHolder extends RecyclerView.ViewHolder{
        public TextView tvWifiName;

        public WifiViewHolder(@NonNull View itemView) {
            super(itemView);

            tvWifiName = itemView.findViewById(R.id.tv_wifiName);

            //와이파이 선택 확인
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        String ssid = items.get(pos).SSID;
                        Log.d("wifi","wifiAdapter : " + ssid);
                        //dialog 호출
                        wifiDialog customDialog = new wifiDialog(wContext);
                        customDialog.callFunction(ssid);
                    }
                }
            });

            tvWifiName=itemView.findViewById(R.id.tv_wifiName);
        }
        public void setItem(ScanResult item){
            tvWifiName.setText(item.SSID);
        }
    }
}
