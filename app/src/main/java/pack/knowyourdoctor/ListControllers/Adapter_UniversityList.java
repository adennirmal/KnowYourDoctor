package pack.knowyourdoctor.ListControllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import Models.UniversityModel;
import pack.knowyourdoctor.R;

public class Adapter_UniversityList extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<UniversityModel> universities;

    public Adapter_UniversityList(Context context, ArrayList<UniversityModel> array_list) {
        this.context = context;
        universities = array_list;
    }

    @Override
    public int getCount() {
        return universities.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.view_university, parent, false);

        //Get university from position
        UniversityModel currentUni = universities.get(position);

        // Locate the TextViews
        TextView name = (TextView) itemView.findViewById(R.id.name);
        TextView address = (TextView) itemView.findViewById(R.id.address);
        TextView qualification = (TextView) itemView.findViewById(R.id.qualification);

        name.setText(currentUni.getName());
        address.setText(currentUni.getAddress());
        qualification.setText(currentUni.getQualification());

        return itemView;
    }

}
