package org.evilsoft.pathfinder.reference.list;

import org.evilsoft.pathfinder.reference.DisplayListAdapter;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

public class CharacterListAdapter extends DisplayListAdapter {

    public CharacterListAdapter(Context context, Cursor c, String character_id) {
        super(context, c);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return convertView;
    }

    @Override
    public Object buildItem(Cursor c) {
        // TODO Auto-generated method stub
        return null;
    }

}
