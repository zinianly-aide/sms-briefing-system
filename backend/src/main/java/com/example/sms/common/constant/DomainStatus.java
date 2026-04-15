package com.example.sms.common.constant;

public final class DomainStatus {
    private DomainStatus() {}

    public static final class Contact {
        public static final String ACTIVE = "active";
        public static final String INACTIVE = "inactive";

        private Contact() {}
    }

    public static final class Group {
        public static final String ENABLED = "enabled";
        public static final String DISABLED = "disabled";

        private Group() {}
    }

    public static final class Template {
        public static final String ACTIVE = "active";
        public static final String DRAFT = "draft";

        private Template() {}
    }

    public static final class Briefing {
        public static final String DRAFT = "draft";
        public static final String PENDING_REVIEW = "pending_review";
        public static final String PENDING_SEND = "pending_send";
        public static final String SENT = "sent";

        private Briefing() {}
    }

    public static final class Task {
        public static final String DRAFT = "draft";
        public static final String PENDING = "pending";
        public static final String SENDING = "sending";
        public static final String COMPLETED = "completed";
        public static final String PARTIAL_SUCCESS = "partial_success";
        public static final String FAILED = "failed";
        public static final String CANCELLED = "cancelled";

        private Task() {}
    }

    public static final class Recipient {
        public static final String PENDING = "pending";
        public static final String SUCCESS = "success";
        public static final String FAILED = "failed";

        private Recipient() {}
    }

    public static final class Channel {
        public static final String SMS = "sms";
        public static final String SMS_WECOM = "sms_wecom";

        private Channel() {}
    }
}
