package junzhaosun.map.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import junzhaosun.map.R;

public class ListItemAdapter extends BaseAdapter {
    private List<String> list;
    private final Context context;
    private final LayoutInflater inflater;
    private ViewHolder holder;
    private mListener listener;

    public ListItemAdapter(Context context, List<String> list){
        this.context = context;
        this.list = list;
        this.inflater=LayoutInflater.from(context);
    }

    public void remove(int position){
        if(position>=0 && position<list.size())
            list.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ViewHolder{
        TextView text_item;
        ImageView deleteButton;
    }

    public void setOnClickListener(mListener listener) {

        this.listener = listener;

    }

    private View.OnClickListener mOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = (Integer) v.getTag();
                switch (v.getId()) {
                    case R.id.item:
                        listener.onShowLocation(ListItemAdapter.this, v, position);
                        break;
                    case R.id.delete:
                        listener.onDeleteLocation(ListItemAdapter.this, v, position);
                        break;
                }
            }
        }
    };

    public  interface  mListener {
        public void  onShowLocation(BaseAdapter adapter, View view, int position);
        public void  onDeleteLocation(ListItemAdapter adapter, View view, int position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder=new ViewHolder();
        convertView=inflater.inflate(R.layout.list_item_adapter, null);

        holder.text_item=(TextView) convertView.findViewById(R.id.item);
        holder.text_item.setText(list.get(position));
        holder.deleteButton=(ImageView) convertView.findViewById(R.id.delete);

        holder.text_item.setOnClickListener(mOnClickListener);
        holder.deleteButton.setOnClickListener(mOnClickListener);

        convertView.setTag(holder);
        holder.deleteButton.setTag(position);
        holder.text_item.setTag(position);

        return convertView;
    }

}
