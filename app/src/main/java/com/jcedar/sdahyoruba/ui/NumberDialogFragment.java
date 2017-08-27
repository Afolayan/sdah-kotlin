package com.jcedar.sdahyoruba.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcedar.sdahyoruba.BuildConfig;
import com.jcedar.sdahyoruba.R;
import com.jcedar.sdahyoruba.app.SdahYoruba;
import com.jcedar.sdahyoruba.provider.DatabaseHelper;

/**
 * Created by Afolayan Oluwaseyi on 04/06/2016.
 */
public class NumberDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = NumberDialogFragment.class.getSimpleName();
    TextView numberView;
    Button button1, button2, button3,button4,
            button5,button6,button7,button8,button9, button0;
    ImageButton button;
    private DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            if( !TextUtils.isEmpty( numberView.getText())){
                long value = Long.valueOf( numberView.getText().toString());

                if( value > 0 ){
                    goToSelectedHymn(value);
                }
            }
        }
    };
    private DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };
    private View.OnClickListener backSpaceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String string = numberView.getText().toString();
            if( !TextUtils.isEmpty( string )){
                int len = string.length();
                if(len == 1){
                    numberView.setText(BuildConfig.FLAVOR);
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder(string);
                stringBuilder.deleteCharAt(len - 1);
                numberView.setText(stringBuilder.toString());
            }
        }
    };

    public NumberDialogFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.layout_number_pad, null, false);
        numberView = (TextView) view.findViewById(R.id.hymnField);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.buttonLayout1);
        button1 = (Button)layout.findViewById(R.id.button1); button1.setText("1");button1.setOnClickListener(this);
        button2 = ((Button)layout.findViewById(R.id.button2));button2.setText("2");button2.setOnClickListener(this);
        button3 = ((Button)layout.findViewById(R.id.button3)); button3.setText("3"); button3.setOnClickListener(this);

         layout = (LinearLayout) view.findViewById(R.id.buttonLayout2);
        button4 = (Button)layout.findViewById(R.id.button1); button4.setText("4");button4.setOnClickListener(this);
        button5 = ((Button)layout.findViewById(R.id.button2));button5.setText("5");button5.setOnClickListener(this);
        button6 = ((Button)layout.findViewById(R.id.button3)); button6.setText("6"); button6.setOnClickListener(this);

         layout = (LinearLayout) view.findViewById(R.id.buttonLayout3);
        button7 = (Button)layout.findViewById(R.id.button1); button7.setText("7");button7.setOnClickListener(this);
        button8 = ((Button)layout.findViewById(R.id.button2));button8.setText("8");button8.setOnClickListener(this);
        button9 = ((Button)layout.findViewById(R.id.button3)); button9.setText("9"); button9.setOnClickListener(this);

        layout = (LinearLayout) view.findViewById(R.id.zeroLayout);
        button0 = ((Button)layout.findViewById(R.id.button2)); button0.setText("0"); button0.setOnClickListener(this);

        button = (ImageButton) view.findViewById(R.id.btnBackSpace);
        button.setOnClickListener( backSpaceListener );

        SdahYoruba.getInstance().trackScreenView(TAG);
        return new AlertDialog.Builder(getActivity()).setView(view)
                .setNegativeButton(R.string.mdtp_cancel, negativeListener)
                .setPositiveButton(R.string.mdtp_ok, positiveListener).create();
    }

    @Override
    public void onClick(View v) {
        numberView.append(((Button) v).getText());
        String num = Integer.valueOf( numberView.getText().toString() ) + BuildConfig.FLAVOR;
        numberView.setText(num);

        int maxNumber = new DatabaseHelper(getActivity()).getHymnsSize();

        if(Integer.parseInt( numberView.getText().toString() ) > maxNumber){
            numberView.setText( String.valueOf(maxNumber) );
        }
    }

    public int goToSelectedHymn(long hymnId) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if( getActivity().getSupportFragmentManager().findFragmentById(R.id.frame) != null){

            fm.beginTransaction()
                    .remove(getActivity().getSupportFragmentManager()
                            .findFragmentById(R.id.frame))
                    .commit();
        }

        FragmentTransaction ft1 =getActivity().getSupportFragmentManager().beginTransaction();
        ft1.add(R.id.frame, PagerFragment.newInstance(hymnId), "HOME")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
         
        return 0;
    }
}
