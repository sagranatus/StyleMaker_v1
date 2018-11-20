package com.sagra.stylemaker_v1.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

public class ListSelectorDialog {
    Context context;
    Builder adb;
    String title;

    // our interface so we can return the selected key/item pair.
    public interface listSelectorInterface {
        void selectedItem(String key, String item);
        void selectorCanceled();
    }

    public ListSelectorDialog(Context c, String newTitle) {
        this.context = c;
        this.title = newTitle;
    }

    ListSelectorDialog setTitle(String newTitle) {
        this.title = newTitle;
        return this;
    }


    public ListSelectorDialog show(final String[] itemList, final String[] keyList,
                                   final listSelectorInterface di) {

        // set up the dialog
        adb = new Builder(context);
        adb.setCancelable(false);

        adb.setItems(itemList, new DialogInterface.OnClickListener() {
            // when an item is clicked, notify our interface
            public void onClick(DialogInterface d, int n) {
                d.dismiss();
                di.selectedItem(keyList[n], itemList[n]);
            }
        });
    /*    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            // when user clicks cancel, notify our interface
            public void onClick(DialogInterface d, int n) {
                d.dismiss();
                di.selectorCanceled();

            }
        }); */
       // adb.setTitle(title);
        // show the dialog
        AlertDialog dialog = adb.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
     //   adb.show();
        return this;
    }






}







