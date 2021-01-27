package tracesgps.ui.statistics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import tracesgps.R;

/**
 * Adapteur pour une ExpandableListView.
 * @author Jennifer Viney.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    // ATTRIBUTS

    private Context context;
    private List<String> _listDataheader;
    private HashMap<String, List<String>> _listDataChild;

    // CONSTRUCTEURS

    ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listDataChild) {
        this.context = context;
        _listDataheader = listDataHeader;
        _listDataChild = listDataChild;
    }

    // COMMANDES

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(
                _listDataChild.get(_listDataheader.get(groupPosition)))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView,
                             ViewGroup parent) {
        final String childText = (String) getChild(
                groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(layoutInflater).
                    inflate(R.layout.list_item, null);
        }

        TextView textListChild = convertView
                .findViewById(R.id.label_list_item);

        textListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(_listDataChild
                .get(_listDataheader.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return _listDataheader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return _listDataheader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(layoutInflater)
                    .inflate(R.layout.list_group, null);
        }

        TextView labelListHeader = convertView
                .findViewById(R.id.label_list_header);
        labelListHeader.setTypeface(null, Typeface.BOLD);
        labelListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
