package com.wlb.agent.ui.user.frag;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.android.util.text.StringUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.AuthStatus;
import com.wlb.agent.core.data.user.entity.EducationInfo;
import com.wlb.agent.core.data.user.entity.ProfessionalInfo;
import com.wlb.agent.core.data.user.entity.QualificationInfo;
import com.wlb.agent.core.data.user.response.AuthenticationCommResponse;
import com.wlb.agent.core.data.user.response.AuthenticationResponse;
import com.wlb.agent.ui.common.view.CommonPopupWindow;
import com.wlb.agent.ui.common.view.PhotoPickSheet;
import com.wlb.agent.ui.common.view.UploadPhoto;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.imgloader.ImageFactory;
import com.wlb.common.imgloader.ImageLoaderImpl;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;
import common.widget.dialog.loading.LoadingDialog;
import common.widget.viewpager.ViewPager;

/**
 * 认证
 * <p/>
 * Created by 曹泽琛.
 */
public class VerifyFrag extends SimpleFrag implements View.OnClickListener {

    private View viewProfessionTip, viewEducationTip, viewQualificationTip;
    private List<View> viewList;//view数组
    private TextView etProfessionCompany, etProfessionPost;
    private TextView tvProfessionStatus;
    private ImageView imgProfession;
    private TextView tvProfessionTip;
    private Button btnProfessionCommit;

    private TextView etEducationSchool, etEducationSubject;
    private TextView etEducation, tvEducationStatus;
    private TextView tvEducationStart, tvEducationEnd;
    private ImageView imgEducation;
    private TextView tvEducationTip;
    private Button btnEducationCommit;

    private TextView etQualificationCompany, etQualificationPost;
    private TextView tvQualificationStatus;
    private TextView tvQualificationDate;
    private ImageView imgQualification;
    private TextView tvQualificationTip;
    private Button btnQualificationCommit;
    private DateDialog startDialog, endDialog, dateDialog;
    @BindView(R.id.verify_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.verify_profession_view)
    View professionView;
    @BindView(R.id.verify_education_view)
    View educationView;
    @BindView(R.id.verify_qualification_view)
    View qualificationView;
    @BindView(R.id.verify_refresh)
    SwipeRefreshLayout verifyRefresh;
    private int type = 1;//1是职业  2是教育  3是资格 用于照片返回
    private String saveProfession, saveEducation, saveQualification;
    private Task getVerifyTask, changeProfessionTask, changeEducationTask, changeQualificationTask;
    private ProfessionalInfo professionalInfo = new ProfessionalInfo();
    private EducationInfo educationInfo = new EducationInfo();
    private QualificationInfo qualificationInfo = new QualificationInfo();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
    private CommonPopupWindow popWindow;
    private PhotoPickSheet photoPickSheet;
    private static final String PARAM = "param";
    public static final int switchProfession = 0;
    public static final int switchEducation = 1;
    public static final int switchQualification = 2;

    public static SimpleFragAct.SimpleFragParam getStartParam(int switchTab) {
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM, switchTab);
        return new SimpleFragAct.SimpleFragParam("认证", VerifyFrag.class, bundle);
    }

    public static void start(Context context, int switchTab) {
        SimpleFragAct.start(context, getStartParam(switchTab));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.verify_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initAddView();
        initSetAdapter();
        initView();
        switchPager(getArguments().getInt(PARAM));
        getVerifyInfo();
    }

    @OnClick({R.id.verify_profession, R.id.verify_education, R.id.verify_qualification})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verify_profession://职业认证
                switchPager(0);
                break;
            case R.id.verify_education://教育认证
                switchPager(1);
                break;
            case R.id.verify_qualification://资格认证
                switchPager(2);
                break;
            case R.id.education_item_start://选择教育开始时间
                if (null == startDialog) {
                    startDialog = new DateDialog(tvEducationStart).setDate(Calendar.getInstance());
                }
                startDialog.show();
                break;
            case R.id.education_item_end://选择教育截止时间
                if (null == endDialog) {
                    endDialog = new DateDialog(tvEducationEnd).setDate(Calendar.getInstance());
                }
                endDialog.show();
                break;
            case R.id.qualification_item_start://资格认证发证日期
                if (null == dateDialog) {
                    dateDialog = new DateDialog(tvQualificationDate).setDate(Calendar.getInstance());
                }
                dateDialog.show();
                break;
            case R.id.education_item_educational://选择学历
                showInsurancePop(etEducation);

                break;
            case R.id.profession_item_commit:
                String professionComany = etProfessionCompany.getText().toString();
                if (TextUtils.isEmpty(professionComany)) {
                    ToastUtil.show("请输入公司名称");
                    return;
                }
                String professionPost = etProfessionPost.getText().toString();
                if (TextUtils.isEmpty(professionPost)) {
                    ToastUtil.show("请输入职位名称");
                    return;
                }
                if (professionPost.length() > 12) {
                    ToastUtil.show("职位名称不能超过12个字");
                    return;
                }

                /*if (TextUtils.isEmpty(saveProfession)) {
                    ToastUtil.show("请上传图片");
                    return;
                }*/

                changeProfession(professionComany, professionPost, saveProfession);
                break;
            case R.id.education_item_commit:
                String school = etEducationSchool.getText().toString();
                if (TextUtils.isEmpty(school)) {
                    ToastUtil.show("请输入学校名称");
                    return;
                }
                educationInfo.school = school;
                String subject = etEducationSubject.getText().toString();
                if (TextUtils.isEmpty(subject)) {
                    ToastUtil.show("请输入专业");
                    return;
                }
                educationInfo.subject = subject;
                String education = etEducation.getText().toString();
                if (TextUtils.isEmpty(education)) {
                    ToastUtil.show("请选择学历");
                    return;
                }
                educationInfo.educational = education;
                String startTime = StringUtil.trim(tvEducationStart);
                if (TextUtils.isEmpty(startTime)) {
                    ToastUtil.show("请选择入学日期");
                    return;
                }
                try {
                    Date dateStart = format.parse(startTime);
                    educationInfo.enrollment_time = dateStart.getTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String endTime = StringUtil.trim(tvEducationEnd);
                if (TextUtils.isEmpty(endTime)) {
                    ToastUtil.show("请输入毕业日期");
                    return;
                }
                try {
                    Date dateEnd = format.parse(endTime);
                    educationInfo.graduation_time = dateEnd.getTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (TextUtils.isEmpty(saveEducation)) {
                    ToastUtil.show("请上传图片");
                    return;
                }

                educationInfo.cert_url = saveEducation;
                changeEducation(educationInfo);
                break;
            case R.id.qualification_item_commit:
                String certificate = etQualificationCompany.getText().toString();
                if (TextUtils.isEmpty(certificate)) {
                    ToastUtil.show("请输入证书名称");
                    return;
                }
                qualificationInfo.certificate = certificate;
                String cert_no = etQualificationPost.getText().toString();
                if (TextUtils.isEmpty(cert_no)) {
                    ToastUtil.show("请输入证书号码");
                    return;
                }
                qualificationInfo.cert_no = cert_no;

                String dateTime = StringUtil.trim(tvQualificationDate);
                if (TextUtils.isEmpty(dateTime)) {
                    ToastUtil.show("请选择发证日期");
                    return;
                }
                try {
                    Date date = format.parse(dateTime);
                    qualificationInfo.award_time = date.getTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (TextUtils.isEmpty(saveQualification)) {
                    ToastUtil.show("请上传图片");
                    return;
                }

                qualificationInfo.cert_url = saveQualification;
                changeQualification(qualificationInfo);
                break;
            case R.id.profession_item_tip://职业认证提示图片
                initPop(1);
                popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.education_item_tip://教育认证提示图片
                initPop(2);
                popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.qualification_item_tip://资格认证提示图片
                initPop(3);
                popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.profession_tip_close:
                popWindow.dismiss();
                break;
            case R.id.profession_item_img://选择职业认证图片
                type = 1;
                photoPickSheet.show();
                break;
            case R.id.education_item_img://选择教育认证图片
                type = 2;
                photoPickSheet.show();
                break;
            case R.id.qualification_item_img://选择资格认证图片
                type = 3;
                photoPickSheet.show();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != photoPickSheet) {
            photoPickSheet.release();
        }
        if (null != getVerifyTask) {
            getVerifyTask.stop();
        }
        if (null != changeProfessionTask) {
            changeProfessionTask.stop();
        }
        if (null != changeEducationTask) {
            changeEducationTask.stop();
        }
        if (null != changeQualificationTask) {
            changeQualificationTask.stop();
        }
    }

    private void initView() {

        photoPickSheet = new PhotoPickSheet(this);
        photoPickSheet.setReqSize(500);


        //职业认证
        etProfessionCompany = (TextView) viewProfessionTip.findViewById(R.id.profession_item_company);
        etProfessionPost = (TextView) viewProfessionTip.findViewById(R.id.profession_item_post);
        tvProfessionStatus = (TextView) viewProfessionTip.findViewById(R.id.profession_item_status);
        imgProfession = (ImageView) viewProfessionTip.findViewById(R.id.profession_item_img);
        imgProfession.setOnClickListener(this);
        btnProfessionCommit = (Button) viewProfessionTip.findViewById(R.id.profession_item_commit);
        btnProfessionCommit.setOnClickListener(this);
        viewProfessionTip.findViewById(R.id.profession_item_tip).setOnClickListener(this);
        tvProfessionTip = (TextView) viewProfessionTip.findViewById(R.id.profession_tip);
        //教育认证
        etEducationSchool = (TextView) viewEducationTip.findViewById(R.id.education_item_school);
        etEducationSubject = (TextView) viewEducationTip.findViewById(R.id.education_item_subject);
        etEducation = (TextView) viewEducationTip.findViewById(R.id.education_item_educational);
        etEducation.setOnClickListener(this);
        tvEducationStatus = (TextView) viewEducationTip.findViewById(R.id.education_item_status);
        tvEducationStart = (TextView) viewEducationTip.findViewById(R.id.education_item_start);
        tvEducationStart.setOnClickListener(this);
        tvEducationEnd = (TextView) viewEducationTip.findViewById(R.id.education_item_end);
        tvEducationEnd.setOnClickListener(this);
        imgEducation = (ImageView) viewEducationTip.findViewById(R.id.education_item_img);
        imgEducation.setOnClickListener(this);
        btnEducationCommit = (Button) viewEducationTip.findViewById(R.id.education_item_commit);
        btnEducationCommit.setOnClickListener(this);
        viewEducationTip.findViewById(R.id.education_item_tip).setOnClickListener(this);
        tvEducationTip = (TextView) viewEducationTip.findViewById(R.id.education_tip);
        //资格认证
        etQualificationCompany = (TextView) viewQualificationTip.findViewById(R.id.qualification_item_company);
        etQualificationPost = (TextView) viewQualificationTip.findViewById(R.id.qualification_item_post);
        tvQualificationStatus = (TextView) viewQualificationTip.findViewById(R.id.qualification_item_status);
        tvQualificationDate = (TextView) viewQualificationTip.findViewById(R.id.qualification_item_start);
        tvQualificationDate.setOnClickListener(this);
        imgQualification = (ImageView) viewQualificationTip.findViewById(R.id.qualification_item_img);
        imgQualification.setOnClickListener(this);
        btnQualificationCommit = (Button) viewQualificationTip.findViewById(R.id.qualification_item_commit);
        btnQualificationCommit.setOnClickListener(this);
        viewQualificationTip.findViewById(R.id.qualification_item_tip).setOnClickListener(this);
        tvQualificationTip = (TextView) viewQualificationTip.findViewById(R.id.qualification_tip);

        verifyRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //执行网络请求
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    ToastUtil.show(R.string.net_noconnection);
                    verifyRefresh.setRefreshing(false);
                    return;
                }
                getVerifyInfo();
            }
        });
    }

    private void initPop(int tipStyle) {
        popWindow = new CommonPopupWindow(mContext, R.layout.verify_photo_tip,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, R.style.timepopwindow_anim_style);
        popWindow.findViewById(R.id.profession_tip_close).setOnClickListener(this);
        TextView tvTipName = (TextView) popWindow.findViewById(R.id.profession_tip_name);
        ImageView imgTip = (ImageView) popWindow.findViewById(R.id.profession_tip_img);
        TextView tvTipOne = (TextView) popWindow.findViewById(R.id.profession_tip_one);
        TextView tvTipTwo = (TextView) popWindow.findViewById(R.id.profession_tip_two);
        if (1 == tipStyle) {//名片拍摄技巧
            tvTipName.setText("名片拍摄技巧");
            imgTip.setImageResource(R.drawable.profession_tip);
            tvTipOne.setText("1、须确保公司职位拍摄清晰。");
            tvTipTwo.setText("2、如果材料中含手机号等隐私信息，系统将自动打码处理。");
        } else if (2 == tipStyle) {//毕业证拍摄技巧
            tvTipName.setText("毕业证拍摄技巧");
            imgTip.setImageResource(R.drawable.education_tip);
            tvTipOne.setText("1、确保姓名、学校区域拍摄清晰。");
            tvTipTwo.setText("2、如果材料中含隐私信息，建议手指挡住局部拍摄。");
        } else if (3 == tipStyle) {//资格认证拍摄技巧
            tvTipName.setText("资格证书拍摄技巧");
            imgTip.setImageResource(R.drawable.qualificatio_tip);
            tvTipOne.setText("1、须确保资格证书号码拍摄清晰。");
            tvTipTwo.setText("2、如果材料中含手机号等隐私信息，建议手指挡住局部拍摄。");
        }
    }

    private void showInsurancePop(final TextView tvView) {

        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        ListView mListView = (ListView) View.inflate(mContext, R.layout.common_listview, null);
        mListView.setDivider(getResources().getDrawable(R.drawable.line));
        dialog.setContentView(mListView);

        String[] educationArray = getResources().getStringArray(R.array.educations);
        final List<String> listData = Arrays.asList(educationArray);

        mListView.setAdapter(new ListAdapter<String>(mContext,
                listData, R.layout.verify_education_listview_item) {

            @Override
            public void setViewData(ViewHolder holder, String data) {
                holder.setText(R.id.education_text, data);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvView.setText(listData.get(position));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean isGetVerify;

    private void getVerifyInfo() {
        if (isGetVerify) return;

        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            showToastShort(R.string.net_noconnection);
            verifyRefresh.setRefreshing(false);

            return;
        }
        getVerifyTask = UserClient.doGetAuthemticationInfo(new ICallback() {
            @Override
            public void start() {
                isGetVerify = true;
            }

            @Override
            public void success(Object data) {
                AuthenticationResponse response = (AuthenticationResponse) data;
                if (response.isSuccessful()) {
                    professionalInfo = response.professional;
                    if (null != professionalInfo)
                        setProfessionView(professionalInfo, professionalInfo.getAuthStatus());
                    educationInfo = response.education;
                    if (null != educationInfo)
                        setEducationView(educationInfo, educationInfo.getAuthStatus());
                    qualificationInfo = response.qualification;
                    if (null != qualificationInfo)
                        setQualificationView(qualificationInfo, qualificationInfo.getAuthStatus());
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                isGetVerify = false;
                verifyRefresh.setRefreshing(false);
            }
        });
    }

    private boolean isChangeProfession;

    private void changeProfession(String company_name, String professional_name, String cert_url) {
        if (isChangeProfession) return;
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            showToastShort(R.string.net_noconnection);
            return;
        }
        final LoadingDialog loadingDialog = new LoadingDialog(getContext(),
                "正在上传...").setCancelable(false).show();
        changeProfessionTask = UserClient.doChangeProfessional(company_name, professional_name, cert_url,
                new ICallback() {
                    @Override
                    public void start() {
                        isChangeProfession = true;
                    }

                    @Override
                    public void success(Object data) {
                        AuthenticationCommResponse response = (AuthenticationCommResponse) data;
                        if (response.isSuccessful()) {
                            updateProfessionStatus(response.getAuthStatus());
                        } else {
                            ToastUtil.show(response.msg);
                        }
                    }

                    @Override
                    public void failure(NetException e) {
                        ToastUtil.show(R.string.req_fail);
                    }

                    @Override
                    public void end() {
                        isChangeProfession = false;
                        loadingDialog.dissmiss();
                    }
                });
    }

    private boolean isChangeEducation;

    private void changeEducation(final EducationInfo educationInfo) {
        if (isChangeEducation) return;
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            showToastShort(R.string.net_noconnection);
            return;
        }
        final LoadingDialog loadingDialog = new LoadingDialog(getContext(),
                "正在上传...").setCancelable(false).show();
        changeEducationTask = UserClient.doChangeEducation(educationInfo, new ICallback() {
            @Override
            public void start() {
                isChangeEducation = true;
            }

            @Override
            public void success(Object data) {
                AuthenticationCommResponse response = (AuthenticationCommResponse) data;
                if (response.isSuccessful()) {
                    updateEducationStatus(response.getAuthStatus());
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                isChangeEducation = false;
                loadingDialog.dissmiss();
            }
        });
    }

    private boolean isChangeQualification;

    private void changeQualification(final QualificationInfo qualificationInfo) {
        if (isChangeQualification) return;
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            showToastShort(R.string.net_noconnection);
            return;
        }
        final LoadingDialog loadingDialog = new LoadingDialog(getContext(),
                "正在上传...").setCancelable(false).show();
        changeQualificationTask = UserClient.doChangeQualification(qualificationInfo, new ICallback() {
            @Override
            public void start() {
                isChangeQualification = true;
            }

            @Override
            public void success(Object data) {
                AuthenticationCommResponse response = (AuthenticationCommResponse) data;
                if (response.isSuccessful()) {
                    updateQualicationStatus(response.getAuthStatus());
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                isChangeQualification = false;
                loadingDialog.dissmiss();
            }
        });
    }


    private void updateProfessionStatus(AuthStatus auth_tatus) {
        switch (auth_tatus) {
            case AUTH_NOT:
                break;
            case AUTHING:
                tvProfessionStatus.setVisibility(View.VISIBLE);
                tvProfessionStatus.setText("审核中");
                tvProfessionStatus.setBackgroundResource(R.drawable.shape_f39900);
               /* etProfessionCompany.setEnabled(false);
                etProfessionPost.setEnabled(false);
                imgProfession.setEnabled(false);*/
                btnProfessionCommit.setText("更新审核资料");
                break;
            case AUTH_FAIL:
                tvProfessionStatus.setVisibility(View.VISIBLE);
                tvProfessionStatus.setText("未通过");
                tvProfessionStatus.setBackgroundResource(R.drawable.shape_00alea);
               /* etProfessionCompany.setEnabled(true);
                etProfessionPost.setEnabled(true);
                imgProfession.setEnabled(true);*/
                btnProfessionCommit.setText("重新认证");
                break;
            case AUTH_SUCCESS:
                tvProfessionStatus.setVisibility(View.VISIBLE);
                tvProfessionStatus.setText("已认证");
                tvProfessionStatus.setBackgroundResource(R.drawable.shape_50b970);
               /* etProfessionCompany.setEnabled(true);
                etProfessionPost.setEnabled(true);
                imgProfession.setEnabled(true);*/
                btnProfessionCommit.setText("重新认证");
                break;
        }
    }

    private void setProfessionView(ProfessionalInfo professionalInfo, AuthStatus auth_tatus) {
        //审核状态  0未认证，1 审核中  2审核失败 3已认证
        if (null != professionalInfo) {
            etProfessionCompany.setText(professionalInfo.company_name);
            etProfessionPost.setText(professionalInfo.professional_name);
            DisplayImageOptions imageOptions = getImageOptions();
            ImageFactory.getUniversalImpl().getImg(professionalInfo.cert_url, imgProfession, null, imageOptions);
            loadBitmap(professionalInfo.cert_url, 1);
            tvProfessionTip.setText(professionalInfo.tip);
            updateProfessionStatus(auth_tatus);
        }
    }

    private void updateEducationStatus(AuthStatus auth_status) {
        switch (auth_status) {
            case AUTH_NOT:
                break;
            case AUTHING:
               /* etEducationSchool.setEnabled(false);
                etEducationSubject.setEnabled(false);
                etEducation.setEnabled(false);*/
                tvEducationStatus.setVisibility(View.VISIBLE);
                tvEducationStatus.setText("审核中");
                tvEducationStatus.setBackgroundResource(R.drawable.shape_f39900);
              /*  tvEducationStart.setEnabled(false);
                tvEducationEnd.setEnabled(false);
                imgEducation.setEnabled(false);*/
                btnEducationCommit.setText("更新审核资料");
                break;
            case AUTH_FAIL:
               /* etEducationSchool.setEnabled(true);
                etEducationSubject.setEnabled(true);
                etEducation.setEnabled(true);*/
                tvEducationStatus.setVisibility(View.VISIBLE);
                tvEducationStatus.setText("未通过");
                tvEducationStatus.setBackgroundResource(R.drawable.shape_00alea);
               /* tvEducationStart.setEnabled(true);
                tvEducationEnd.setEnabled(true);
                imgEducation.setEnabled(true);*/
                btnEducationCommit.setText("重新认证");
                break;
            case AUTH_SUCCESS:
               /* etEducationSchool.setEnabled(true);
                etEducationSubject.setEnabled(true);
                etEducation.setEnabled(true);*/
                tvEducationStatus.setVisibility(View.VISIBLE);
                tvEducationStatus.setText("已认证");
                tvEducationStatus.setBackgroundResource(R.drawable.shape_50b970);
               /* tvEducationStart.setEnabled(true);
                tvEducationEnd.setEnabled(true);
                imgEducation.setEnabled(true);*/
                btnEducationCommit.setText("重新认证");
                break;
        }
    }

    private void setEducationView(EducationInfo educationInfo, AuthStatus auth_status) {
        //审核状态  0未认证，1 审核中  2审核失败 3已认证
        if (null != educationInfo) {
            etEducationSchool.setText(educationInfo.school);
            etEducationSubject.setText(educationInfo.subject);
            etEducation.setText(educationInfo.educational);
            if (educationInfo.enrollment_time > 0) {
                tvEducationStart.setText(format.format(educationInfo.enrollment_time));
            }
            if (educationInfo.graduation_time > 0) {
                tvEducationEnd.setText(format.format(educationInfo.graduation_time));
            }
            DisplayImageOptions imageOptions = getImageOptions();
            ImageFactory.getUniversalImpl().getImg(educationInfo.cert_url, imgEducation, null, imageOptions);
            loadBitmap(educationInfo.cert_url, 2);
            tvEducationTip.setText(educationInfo.tip);
            updateEducationStatus(auth_status);
        }
    }

    private void updateQualicationStatus(AuthStatus auth_status) {
        switch (auth_status) {
            case AUTH_NOT:
                break;
            case AUTHING:
               /* etQualificationCompany.setEnabled(false);
                etQualificationPost.setEnabled(false);*/
                tvQualificationStatus.setVisibility(View.VISIBLE);
                tvQualificationStatus.setText("审核中");
                tvQualificationStatus.setBackgroundResource(R.drawable.shape_f39900);
                /*tvQualificationDate.setEnabled(false);
                imgQualification.setEnabled(false);*/
                btnQualificationCommit.setText("更新审核资料");
                break;
            case AUTH_FAIL:
               /* etQualificationCompany.setEnabled(true);
                etQualificationPost.setEnabled(true);*/
                tvQualificationStatus.setVisibility(View.VISIBLE);
                tvQualificationStatus.setText("未通过");
                tvQualificationStatus.setBackgroundResource(R.drawable.shape_00alea);
              /*  tvQualificationDate.setEnabled(true);
                imgQualification.setEnabled(true);*/
                btnQualificationCommit.setText("重新认证");
                break;
            case AUTH_SUCCESS:
              /*  etQualificationCompany.setEnabled(true);
                etQualificationPost.setEnabled(true);*/
                tvQualificationStatus.setVisibility(View.VISIBLE);
                tvQualificationStatus.setText("已认证");
                tvQualificationStatus.setBackgroundResource(R.drawable.shape_50b970);
                /*tvQualificationDate.setEnabled(true);
                imgQualification.setEnabled(true);*/
                btnQualificationCommit.setText("重新认证");
                break;
        }
    }

    private void setQualificationView(QualificationInfo qualificationInfo, AuthStatus auth_status) {
        //审核状态  0未认证，1 审核中  2审核失败 3已认证
        if (null != qualificationInfo) {
            etQualificationCompany.setText(qualificationInfo.certificate);
            etQualificationPost.setText(qualificationInfo.cert_no);
            if (qualificationInfo.award_time > 0) {
                tvQualificationDate.setText(format.format(qualificationInfo.award_time));
            }
            DisplayImageOptions imageOptions = getImageOptions();
            ImageFactory.getUniversalImpl().getImg(qualificationInfo.cert_url, imgQualification, null, imageOptions);
            loadBitmap(qualificationInfo.cert_url, 3);
            tvQualificationTip.setText(qualificationInfo.tip);

            updateQualicationStatus(auth_status);
        }
    }

    private void refreshView(int type) {
        Bitmap bitmap = photoPickSheet.getBitmap();
        if (null != bitmap) {
            photoPickSheet.dismiss();
            if (1 == type) {
                imgProfession.setImageBitmap(bitmap);
                saveProfession = photoPickSheet.getUploadImg();
            } else if (2 == type) {
                imgEducation.setImageBitmap(bitmap);
                saveEducation = photoPickSheet.getUploadImg();
            } else if (3 == type) {
                imgQualification.setImageBitmap(bitmap);
                saveQualification = photoPickSheet.getUploadImg();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != photoPickSheet && photoPickSheet.isShowing()) {
            photoPickSheet.onActivityResult(requestCode, resultCode, data);
            refreshView(type);//更新UI
        }
    }

    //---------------------------------------- 这是一条完美的分割线 ------------------------------------


    private DisplayImageOptions getImageOptions() {
        return new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.profession_item_tip)//
                .showImageOnFail(R.drawable.profession_item_tip)//
                .showImageOnLoading(R.drawable.profession_item_tip)//
                .cacheOnDisk(true)//
                .decodingOptions(ImageLoaderImpl.getBitmapOptions())//
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    private void initAddView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        viewProfessionTip = inflater.inflate(R.layout.verify_profession_item, null);
        viewEducationTip = inflater.inflate(R.layout.verify_education_item, null);
        viewQualificationTip = inflater.inflate(R.layout.verify_qualification_item, null);

        viewList = new ArrayList<>();// 将要分页显示的View装入数组中
        viewList.add(viewProfessionTip);
        viewList.add(viewEducationTip);
        viewList.add(viewQualificationTip);
    }

    private void initSetAdapter() {
        MyAdapter adapter = new MyAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.setScrollDurationFactor(ViewPager.SWIPE_SCROLLER_FACTOR);
        mViewPager.setOffscreenPageLimit(viewList.size());
        mViewPager.setPagingEnabled(false);
        switchPager(switchProfession);
    }

    private void switchPager(int type) {
        if (switchProfession == type) {
            professionView.setVisibility(View.VISIBLE);
            educationView.setVisibility(View.GONE);
            qualificationView.setVisibility(View.GONE);
            mViewPager.setCurrentItem(0);
        } else if (switchEducation == type) {
            professionView.setVisibility(View.GONE);
            educationView.setVisibility(View.VISIBLE);
            qualificationView.setVisibility(View.GONE);
            mViewPager.setCurrentItem(1);
        } else if (switchQualification == type) {
            professionView.setVisibility(View.GONE);
            educationView.setVisibility(View.GONE);
            qualificationView.setVisibility(View.VISIBLE);
            mViewPager.setCurrentItem(2);
        }
    }

    private class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }
    }

    /**
     * 时间日期
     */
    private class DateDialog {
        private TextView textView;
        private int mYear;
        private int mMonth;
        private int mDay;

        public DateDialog(TextView textView) {
            this.textView = textView;
        }

        public DateDialog setDate(Calendar c) {
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DATE);
            return this;
        }

        public void show() {
            Dialog dialog = new DatePickerDialog(getContext(),
                    mDateSetListener, mYear, mMonth, mDay);
            dialog.show();
        }

        private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view,
                                  int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                updateDisplay();
            }
        };

        public void updateDisplay() {
            textView.setText(new StringBuilder().append(mYear).append("年")
                    .append(mMonth + 1).append("月").append(mDay).append("日")
            );
        }
    }

    /**
     * 如果要左右滑动，打开这个
     */
    /*mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        switchPager(0);
                        break;
                    case 1:
                        switchPager(1);
                        break;
                    case 2:
                        switchPager(2);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });*/
    private void loadBitmap(final String path, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
                    InputStream inputStream = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    String img = UploadPhoto.getImg(bitmap);
                    if (type == 1) {
                        saveProfession = img;
                    } else if (type == 2) {
                        saveEducation = img;
                    } else if (type == 3) {
                        saveQualification = img;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
