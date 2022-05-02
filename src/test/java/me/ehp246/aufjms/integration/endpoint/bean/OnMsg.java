package me.ehp246.aufjms.integration.endpoint.bean;

import org.springframework.stereotype.Service;

import me.ehp246.aufjms.api.annotation.ForJmsType;
import me.ehp246.aufjms.api.endpoint.InstanceScope;

/**
 * @author Lei Yang
 *
 */
@Service
@ForJmsType(value = ".*", scope = InstanceScope.BEAN)
class OnMsg {

}
