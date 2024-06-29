package com.example.wordwave;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class Custom_Viewpager_Adapter extends FragmentStateAdapter {

    static String fn,un,e,p,ppu;
    public Custom_Viewpager_Adapter(FragmentManager fm, Lifecycle lc , String fn,String un,String e,String p,String ppu) {
        super(fm,lc);
        this.fn = fn;
        this.un = un;
        this.e = e;
        this.p = p;
        this.ppu = ppu;
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ChatsFragment(un);
            case 1:
                return new StatusFragment(un);
            case 2:
                return new ProfileFragment(fn,un,e,p,ppu);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
