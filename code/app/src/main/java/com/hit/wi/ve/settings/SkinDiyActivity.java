package com.hit.wi.ve.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.util.ArrayList;
import java.util.List;
import com.hit.wi.ve.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

public class SkinDiyActivity extends FragmentActivity {

    private ViewPager viewPager;
    private RadioGroup rgTab;
    private FragmentPagerAdapter fmAdapter;
    private Fragment fm1;
    private Fragment fm2;
    private List<Fragment> fragmentList=new ArrayList<Fragment>();

    private int currentPageIndex=0;

    private int colorTouchDown;
    private int backColorEditBox;
    private int textColorEditBox;
    private int backColorCandidateBox;
    private int textColorCandidateBox;
    private int shadowColor=0;

    private View vColorTouchDown;
    private View vColorEditBoxBack;
    private View vColorCandidateBoxBack;
    private View vColorEditBoxText;
    private View vColorCandidateBoxText;
    private View vShadow;
    private Button bt_save;
    private Button bt_cancel;


    private  int[] selectedColorsQk;
    private  int[] selectedColorsT9;

    private int[] oldQkColors;
    private int[] oldT9Colors;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skin_diy);
        loadColors();
        initView();
    }

    public void initView(){
        viewPager=(ViewPager)this.findViewById(R.id.viewPager);
        viewPager.setOnPageChangeListener(new MyOnPageChangedListener());
        rgTab=(RadioGroup)super.findViewById(R.id.rgTab);
        initViewPager();
        rgTab.check(0);
        RadioButton rb=(RadioButton)rgTab.getChildAt(0);
        rb.setChecked(true);
        rgTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_qk:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_t9:
                        viewPager.setCurrentItem(1);
                        break;
                }
            }
        });

        vColorEditBoxBack=(View)findViewById(R.id.v_diy_editbox_back);
        vColorEditBoxText=(View)findViewById(R.id.v_diy_editbox_text);
        vColorCandidateBoxBack=(View)findViewById(R.id.v_diy_candidate_back);
        vColorCandidateBoxText=(View)findViewById(R.id.v_diy_candidate_text);
        vColorTouchDown=(View)findViewById(R.id.v_diy_touchdown);
        vShadow=(View)findViewById(R.id.v_diy_shadow);

        vColorEditBoxBack.setOnClickListener(listener);
        vColorEditBoxBack.setBackgroundColor(backColorEditBox);
        vColorEditBoxText.setOnClickListener(listener);
        vColorEditBoxText.setBackgroundColor(textColorEditBox);
        vColorCandidateBoxBack.setOnClickListener(listener);
        vColorCandidateBoxBack.setBackgroundColor(backColorCandidateBox);
        vColorCandidateBoxText.setOnClickListener(listener);
        vColorCandidateBoxText.setBackgroundColor(textColorCandidateBox);
        vColorTouchDown.setOnClickListener(listener);
        vColorTouchDown.setBackgroundColor(colorTouchDown);
        vShadow.setOnClickListener(listener);
        vShadow.setBackgroundColor(shadowColor);

        bt_save=(Button)findViewById(R.id.bt_save);
        bt_cancel=(Button)findViewById(R.id.bt_cancel);
        bt_save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveColors();
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSet();
            }
        });

    }


    public  void initViewPager(){
        fm1=new QKFragment();
        fm2=new T9Fragment();
        fragmentList.add(fm1);
        fragmentList.add(fm2);
        fmAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        viewPager.setAdapter(fmAdapter);

    }
    public int[] getSelectedColorsQk(){
        return selectedColorsQk;
    }
    public void setSelectedColorsQK(int[] colors){
        selectedColorsQk=colors;
    }
    public void setSelectedColorsT9(int[] colors){
        selectedColorsT9=colors;
    }

    public int[] getOldQkColors(){
        return oldQkColors;
    }
    public int[] getOldT9Colors(){
        return oldT9Colors;
    }
    public View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            chooseColor(v);
        }
    };

    public void chooseColor(final View v){

        AlertDialog.Builder builder =new AlertDialog.Builder(SkinDiyActivity.this);
        LayoutInflater inflater=this.getLayoutInflater();
        View view =inflater.inflate(R.layout.color_picker_dialog,null);
        builder.setView(view);
        builder.setTitle("自定义皮肤");

        final ColorPicker picker = (ColorPicker)view.findViewById(R.id.color_picker);
        SVBar svBar = (SVBar)view.findViewById(R.id.color_picker_svbar);
        OpacityBar opacityBar = (OpacityBar)view.findViewById(R.id.color_picker_opacitybar);
        //使用GONE不会占用空间
        RadioGroup rgPickType=(RadioGroup)view.findViewById(R.id.rg_pick_type);
        rgPickType.setVisibility(View.GONE);
        RadioButton rbPickAsQk=(RadioButton)view.findViewById(R.id.rb_pick_asqk);
        rbPickAsQk.setVisibility(View.GONE);
        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.setOldCenterColor(v.getDrawingCacheBackgroundColor());

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                v.setBackgroundColor(picker.getColor());
                switch (v.getId()) {
                    case R.id.v_diy_touchdown:
                        colorTouchDown=picker.getColor();
                        break;
                    case R.id.v_diy_candidate_back:
                        backColorCandidateBox=picker.getColor();
                        break;
                    case R.id.v_diy_candidate_text:
                        textColorCandidateBox=picker.getColor();
                        break;
                    case R.id.v_diy_editbox_back:
                        backColorEditBox=picker.getColor();
                        break;
                    case R.id.v_diy_editbox_text:
                        textColorEditBox=picker.getColor();
                        break;
                    case R.id.v_diy_shadow:
                        shadowColor=picker.getColor();
                        break;
                }
                }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    class MyOnPageChangedListener implements ViewPager.OnPageChangeListener{
        @Override
        public void onPageScrollStateChanged(int state) {

        }
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            currentPageIndex=position;
            RadioButton rb=(RadioButton)rgTab.getChildAt(position);
            rb.setChecked(true);
        }
    }

    public void loadColors(){
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        oldQkColors=new int[8];
        oldT9Colors=new int[2];
        oldQkColors[0]=sp.getInt("diy_backcolor_functionKeys", 0xFF673ab7);
        oldQkColors[1]=sp.getInt("diy_textcolors_functionKeys", 0xFFe8eaf6);
        oldQkColors[2]=sp.getInt("diy_backcolor_quickSymbol", 0xFF673ab7);
        oldQkColors[3]=sp.getInt("diy_textcolors_quickSymbol",0xFFe8eaf6);
        oldQkColors[4]=sp.getInt("diy_backcolor_26keys",0xFF673ab7);
        oldQkColors[5]=sp.getInt("diy_textcolors_26keys",0xFFe8eaf6);
        oldQkColors[6]=sp.getInt("diy_backcolor_bottom",0xFF673ab7);
        oldQkColors[7]=sp.getInt("diy_textcolors_bottom",0xFFe8eaf6);


        oldT9Colors[0]=sp.getInt("diy_backcolor_t9keys",0xFF673ab7);
        oldT9Colors[1]=sp.getInt("diy_textcolors_t9keys",0xFFe8eaf6);

        backColorCandidateBox=sp.getInt("diy_backcolor_candidate",0xFF673ab7);
        textColorCandidateBox=sp.getInt("diy_textcolors_candidate",0xFFe8eaf6);

        backColorEditBox=sp.getInt("diy_backcolor_preEdit",0xFF673ab7);
        textColorEditBox=sp.getInt("diy_textcolors_preEdit",0xFFe8eaf6);

        selectedColorsQk=oldQkColors;
        selectedColorsT9=oldT9Colors;

        colorTouchDown=sp.getInt("diy_backcolor_touchdown",0xFF5677fc);
        shadowColor=sp.getInt("diy_shadow",0xFF00bcd4);
    }

    public void cancelSet(){
        finish();
    }
    public void saveColors(){
        Log.i("Test","saveColors");
        oldT9Colors=selectedColorsT9;
        oldQkColors=selectedColorsQk;
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("diy_backcolor_functionKeys",oldQkColors[0]);
        editor.putInt("diy_textcolors_functionKeys",oldQkColors[1]);
        editor.putInt("diy_backcolor_quickSymbol",oldQkColors[2]);
        editor.putInt("diy_textcolors_quickSymbol",oldQkColors[3]);

        editor.putInt("diy_backcolor_26keys",oldQkColors[4]);
        editor.putInt("diy_textcolors_26keys",oldQkColors[5]);
        editor.putInt("diy_backcolor_bottom",oldQkColors[6]);
        editor.putInt("diy_textcolors_bottom",oldQkColors[7]);

        editor.putInt("diy_backcolor_t9keys",oldT9Colors[0]);
        editor.putInt("diy_textcolors_t9keys",oldT9Colors[1]);


        editor.putInt("diy_backcolor_candidate",backColorCandidateBox);
        editor.putInt("diy_textcolors_candidate",textColorCandidateBox);

        editor.putInt("diy_backcolor_preEdit",backColorEditBox);
        editor.putInt("diy_textcolors_preEdit",textColorEditBox);

        editor.putInt("diy_backcolor_touchdown",colorTouchDown);

        editor.putInt("diy_shadow", shadowColor);

        Intent intent=this.getIntent();
        int position=intent.getIntExtra("position",0);

        editor.putInt("THEME_TYPE",position);
        editor.putBoolean("IS_DIY",true);
        editor.putBoolean("THEME_CHANGED", true);
        editor.putBoolean("THEME_DEF_ON", false);
        editor.commit();
        finish();
    }
}
