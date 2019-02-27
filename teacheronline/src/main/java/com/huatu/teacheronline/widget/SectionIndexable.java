package com.huatu.teacheronline.widget;

import java.util.List;

/**
 * Created by ljzyuhenda on 16/1/15.
 */
public interface SectionIndexable {
    List<String> getSections();

    int getPositionForSection(int section);
}
