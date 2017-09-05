package com.wlb.agent.ui.user.helper.ocr.parser;

import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DataParser {
    public static DrivingLicense parseXml(String result) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write(result.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        return parse(bis);

    }

    public static DrivingLicense parse(InputStream is) throws Exception {
        DrivingLicense drivingLicense = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(is, "UTF-8");
        int eventType = xpp.getEventType();// START_DOCUMENT
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    drivingLicense = new DrivingLicense();
                    break;
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals("cardno")) {
                        eventType = xpp.next();
                        drivingLicense.licenseNo = xpp.getText();
                    } else if (xpp.getName().equals("name")) {
                        eventType = xpp.next();
                        drivingLicense.owner = xpp.getText();
                    } else if (xpp.getName().equals("model")) {
                        eventType = xpp.next();
                        drivingLicense.model = xpp.getText();
                    } else if (xpp.getName().equals("vin")) {
                        eventType = xpp.next();
                        drivingLicense.vin = xpp.getText();
                    } else if (xpp.getName().equals("enginePN")) {
                        eventType = xpp.next();
                        drivingLicense.enginNo = xpp.getText();
                    } else if (xpp.getName().equals("registerDate")) {
                        eventType = xpp.next();
                        String date = xpp.getText();
                        drivingLicense.date=date;
                        if (!TextUtils.isEmpty(date)) {

                            try {
                                drivingLicense.registerDate = dateFormat.parse(date).getTime();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = xpp.next();
        }
        return drivingLicense;
    }
}
