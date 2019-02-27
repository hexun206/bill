package com.huatu.teacheronline.direct.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.teacheronline.R;
import com.huatu.teacheronline.direct.bean.PdfBean;
import com.huatu.teacheronline.widget.CircleView;

import java.util.List;

/**
 * pdf 适配器
 * Created by ljyu on 2017/2/24.
 */
public class DirectClassPdfAdapter extends BaseAdapter {
    public Context context;
    private List<PdfBean> pdfBeanList;

    public DirectClassPdfAdapter(Context context, List<PdfBean> pdfBeanList) {
        this.context = context;
        this.pdfBeanList = pdfBeanList;
    }

    @Override
    public int getCount() {
        return (pdfBeanList == null ? 0 : pdfBeanList.size());
    }

    @Override
    public PdfBean getItem(int position) {
        return (pdfBeanList == null ? null : pdfBeanList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        public TextView tv_pdf_title;
        public ImageView img_pdf_down;
        public CircleView mCircleViewProgress;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pdflist_layout, null);
            holder.tv_pdf_title = (TextView) convertView.findViewById(R.id.tv_pdf_title);
            holder.img_pdf_down = (ImageView) convertView.findViewById(R.id.img_pdf_down);
            holder.mCircleViewProgress = (CircleView) convertView.findViewById(R.id.cv_prograss);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PdfBean bean = pdfBeanList.get(position);
        holder.tv_pdf_title.setText(bean.getTitle());
        holder.mCircleViewProgress.setStrokeWidth(1);
//        String[] split = bean.getFileUrl().split("/");
//        String fileName = split[split.length - 1];
//        String jyDownLoadUrlForLocal = Environment.getExternalStorageDirectory() + "/jy/" + fileName;
//        final File file = new File(jyDownLoadUrlForLocal);

        switch (bean.getState()) {
            case 0:
                holder.img_pdf_down.setImageResource(R.drawable.ic_zj_xiazai);
                break;
            case 1:
                holder.img_pdf_down.setImageResource(R.drawable.ic_jy_right);
                break;
            case 2:
                holder.img_pdf_down.setImageResource(R.drawable.xz_dengdai);
                break;
            case 3:
                holder.img_pdf_down.setImageResource(R.drawable.ic_xz_shibai);
                break;

            case 4:
                holder.img_pdf_down.setImageResource(R.drawable.ic_huancun);
                break;
        }


        if (bean.getState() == 4) {
            holder.mCircleViewProgress.setVisibility(View.VISIBLE);
            holder.mCircleViewProgress.setProgress(bean.getProgress());
        } else {
            holder.mCircleViewProgress.setVisibility(View.GONE);

        }


        return convertView;
    }
}
