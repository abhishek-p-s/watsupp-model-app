package com.example.whatsupp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class tabAccessorAdapter extends FragmentPagerAdapter {

    public tabAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0:
                ChatFragment chatFragment=new ChatFragment();
                return chatFragment;
            case 1:
                GroupFragment groupFragment=new GroupFragment();
                return groupFragment;
            case 2:
                ContactFragment contactFragment=new ContactFragment();
                return contactFragment;
            case 3:
                ResquestsFragment RequestFragment=new ResquestsFragment();
                return RequestFragment;
            default:  return null;
        }


    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "CHATS";
            case 1:
                return "GROUPS";
            case 2:
                return "CONTACTS";
            case 3:
                return "REQUESTS";
            default:  return null;
        }
    }
}
