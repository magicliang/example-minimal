package org.example.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;


/**
 * for example
 */
@Entity
public class LeadsWebActionDataCountAggregate1 {

    @Column(name = "effective_action_type")
    private String effectiveActionType;

    @Column(name = "effective_action_time")
    private Date effectiveActionTime;

    /**
     * this field must comply with this standard： if the sql's column is action_count, its conventional camel
     * spelling is actionCount, if we use
     * <code>
     *  @Column(name = "action_count")
     *     private Integer count;
     *  </code>
     * the column mapping will not work, and we will see empty result record: effectiveActionType and
     * effectiveActionTime will also be null.
     */
    @Column(name = "action_count")
    private Integer actionCount;

    /**
     * get the value of effectiveActionType
     *
     * @return the value of effectiveActionType
     */
    public String getEffectiveActionType() {
        return effectiveActionType;
    }

    /**
     * set the value of the effectiveActionType
     *
     * @param effectiveActionType the value of effectiveActionType
     */
    public void setEffectiveActionType(String effectiveActionType) {
        this.effectiveActionType = effectiveActionType;
    }

    /**
     * get the value of effectiveActionTime
     *
     * @return the value of effectiveActionTime
     */
    public Date getEffectiveActionTime() {
        return effectiveActionTime;
    }

    /**
     * set the value of the effectiveActionTime
     *
     * @param effectiveActionTime the value of effectiveActionTime
     */
    public void setEffectiveActionTime(Date effectiveActionTime) {
        this.effectiveActionTime = effectiveActionTime;
    }

    /**
     * get the value of actionCount
     *
     * @return the value of actionCount
     */
    public Integer getActionCount() {
        return actionCount;
    }

    /**
     * set the value of the actionCount
     *
     * @param actionCount the value of actionCount
     */
    public void setActionCount(Integer actionCount) {
        this.actionCount = actionCount;
    }
}
