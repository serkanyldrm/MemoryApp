package com.example.finalproject;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class MemoriesAdapter extends RecyclerView.Adapter<MemoriesAdapter.MemoryViewHolder> {

    private List<Memory> memories;
    private MemoryListener memoryListener;

    public MemoriesAdapter(List<Memory> memories, MemoryListener memoryListener) {
        this.memories = memories;
        this.memoryListener = memoryListener;
    }

    @NonNull
    @Override
    public MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_memory,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryViewHolder holder, int position) {
        holder.setMemory(memories.get(position));
        holder.layoutMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoryListener.onMemoryClicked(memories.get(position),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return memories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class MemoryViewHolder extends RecyclerView.ViewHolder{

        TextView textTitle,textDateTime;
        ImageView memoryImage;
        LinearLayout layoutMemory;

        MemoryViewHolder(@NonNull View itemView){
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            memoryImage = itemView.findViewById(R.id.memoryImage);
            layoutMemory = itemView.findViewById(R.id.layoutMemory);
        }

        void setMemory(Memory memory){
            textTitle.setText(memory.getTitle());
            textDateTime.setText(memory.getDateTime());

            if(memory.getImagePath() != null){
                memoryImage.setImageBitmap(BitmapFactory.decodeFile(memory.getImagePath()));
                memoryImage.setVisibility(View.VISIBLE);
            }else {
                memoryImage.setVisibility(View.GONE);
            }
        }
    }
}
