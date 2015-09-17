package pack.knowyourdoctor;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class DoctorDataAdapter extends ArrayAdapter<Doctor_Data> {

    private List<Doctor_Data> itemList;
    private Context context;

    public DoctorDataAdapter(List<Doctor_Data> itemList, Context ctx) {
        super(ctx, android.R.layout.simple_list_item_1, itemList);
        this.itemList = itemList;
        this.context = ctx;
    }

    public int getCount() {
        if (itemList != null)
            return itemList.size();
        return 0;
    }

    public Doctor_Data getItem(int position) {
        if (itemList != null)
            return itemList.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (itemList != null)
            return itemList.get(position).hashCode();
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
        }

        Doctor_Data c = itemList.get(position);
        TextView text = (TextView) v.findViewById(R.id.id);
        text.setText("No :"+c.getAutoID());

        TextView text1 = (TextView) v.findViewById(R.id.doctor_name);
        text1.setText("Doctor Name :"+c.getName());

        TextView text2 = (TextView) v.findViewById(R.id.doctor_id);
        text2.setText("Doctor Id :"+c.getID());

        TextView text3 = (TextView) v.findViewById(R.id.updated_at);
        text3.setText(c.getUpdate());

        return v;
    }

    public List<Doctor_Data> getItemList() {
        return itemList;
    }

    public void setItemList(List<Doctor_Data> itemList) {
        this.itemList = itemList;
    }

}
