// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.internal.statistic.eventLog;

import com.intellij.facet.frameworks.SettingsConnectionService;
import com.intellij.internal.statistic.service.fus.FUStatisticsWhiteListGroupsService;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.impl.ApplicationInfoImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

public class EventLogStatisticsSettingsService extends SettingsConnectionService {
  private static final Logger LOG = Logger.getInstance("com.intellij.internal.statistic.eventLog.EventLogStatisticsSettingsService");
  private static final String APPROVED_GROUPS_SERVICE = "white-list-service";
  private static final String PERCENT_TRAFFIC = "percent-traffic";

  public static EventLogStatisticsSettingsService getInstance() {
    return new EventLogStatisticsSettingsService();
  }

  private EventLogStatisticsSettingsService() {
    super(((ApplicationInfoImpl)ApplicationInfoImpl.getShadowInstance()).getEventLogSettingsUrl(), null);
  }

  @NotNull
  @Override
  public String[] getAttributeNames() {
    return ArrayUtil.mergeArrays(super.getAttributeNames(), PERCENT_TRAFFIC, APPROVED_GROUPS_SERVICE);
  }

  public int getPermittedTraffic() {
    final String permitted = getSettingValue(PERCENT_TRAFFIC);
    if (permitted != null) {
      try {
        return Integer.parseInt(permitted);
      }
      catch (NumberFormatException e) {
        LOG.trace("Permitted traffic is not defined or has invalid format: '" + permitted + "'");
      }
    }
    return 0;
  }

  @NotNull
  public Set<String> getWhitelistedGroups() {
    final String approvedGroupsServiceUrl = getSettingValue(APPROVED_GROUPS_SERVICE);
    if (approvedGroupsServiceUrl == null) {
      return Collections.emptySet();
    }
    return FUStatisticsWhiteListGroupsService.getApprovedGroups(getProductRelatedUrl(approvedGroupsServiceUrl));
  }

  @NotNull
  public String getProductRelatedUrl(@NotNull  String approvedGroupsServiceUrl) {
    return approvedGroupsServiceUrl + ApplicationInfo.getInstance().getBuild().getProductCode() + ".json";
  }
}
