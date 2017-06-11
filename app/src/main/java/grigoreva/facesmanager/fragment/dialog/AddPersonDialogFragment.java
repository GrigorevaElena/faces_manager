package grigoreva.facesmanager.fragment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import grigoreva.facesmanager.R;

/**
 * Created by Лена on 11.06.2017.
 */
public class AddPersonDialogFragment extends DialogFragment {
    private EditText mSurnameEditText;
    private EditText mNameEditText;
    private CheckBox mIsContactCheckBox;

    private AddPersonInterface mListener;

    public interface AddPersonInterface {
        void savePerson(String surname, String name, boolean isContact);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddPersonInterface) {
            mListener = (AddPersonInterface) context;
        } else if (getActivity() instanceof AddPersonInterface) {
            mListener = (AddPersonInterface) getActivity();
        } else {
            throw new RuntimeException("Activity or fragment must implements " +
                    AddPersonInterface.class.getCanonicalName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.view_add_new_person, null);
        mSurnameEditText = (EditText) view.findViewById(R.id.add_surname_edit_text);
        mNameEditText = (EditText) view.findViewById(R.id.add_name_edit_text);
        mIsContactCheckBox = (CheckBox) view.findViewById(R.id.add_is_contact_check_box);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle(R.string.add_new_person_title)
                .setPositiveButton(R.string.add_person_to_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String surname = mSurnameEditText.getText().toString();
                        String name = mNameEditText.getText().toString();
                        if (TextUtils.isEmpty(surname) || TextUtils.isEmpty(name)) {
                            Toast.makeText(getActivity(), "Не заполнены обязательные поля", Toast.LENGTH_SHORT).show();
                        } else {
                            mListener.savePerson(surname, name, mIsContactCheckBox.isChecked());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}
