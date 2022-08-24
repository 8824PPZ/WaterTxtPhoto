package com.ppz.watertxtphoto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.huantansheng.easyphotos.models.puzzle.Area;
import com.huantansheng.easyphotos.models.puzzle.PuzzleLayout;
import com.huantansheng.easyphotos.models.sticker.StickerModel;
import com.huantansheng.easyphotos.ui.adapter.TextStickerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextStickerAdapter.OnItemClickListener {


    private RecyclerView rvPuzzleTemplet;
    private TextStickerAdapter textStickerAdapter;

    private RelativeLayout mRootView;

    private StickerModel stickerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }


    private void initUI() {

        stickerModel = new StickerModel();

        mRootView = findViewById(R.id.m_root_view);

        rvPuzzleTemplet = (RecyclerView) findViewById(R.id.rv_puzzle_template);
        rvPuzzleTemplet.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        textStickerAdapter = new TextStickerAdapter(this, this);

        rvPuzzleTemplet.setAdapter(textStickerAdapter);
    }

    @Override
    public void onItemClick(String stickerValue) {

        Log.e("AAAA", stickerValue);
        stickerModel.addTextSticker(this, getSupportFragmentManager(), stickerValue, mRootView);
        //stickerModel.addTextSticker(this, getSupportFragmentManager(), stickerValue, mRootView);
    }
}