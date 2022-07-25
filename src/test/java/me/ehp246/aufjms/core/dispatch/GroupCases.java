package me.ehp246.aufjms.core.dispatch;

import me.ehp246.aufjms.api.annotation.OfGroupId;
import me.ehp246.aufjms.api.annotation.OfGroupSeq;

/**
 * @author Lei Yang
 *
 */
class GroupCases {
    interface Case01 {
        @OfGroupId
        void get();

        @OfGroupId("id")
        void get(@OfGroupId("995ab068-ffae-4382-99fb-b67f8d60b3fa") String id);

        void get(@OfGroupId int i);

        @OfGroupId("${id}")
        void get2();

        @OfGroupId("id")
        void get3();
    }

    interface Case02 {
        @OfGroupId
        void get(@OfGroupSeq int seq);

        @OfGroupId
        void get(@OfGroupSeq Integer seq);

        @OfGroupId
        void get(@OfGroupSeq String seq);
    }
}
