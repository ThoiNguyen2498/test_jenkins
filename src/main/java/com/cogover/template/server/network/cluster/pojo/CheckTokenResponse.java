package com.cogover.template.server.network.cluster.pojo;

import com.cogover.template.server.database.entity.common_mysql.Account;
import com.cogover.template.server.database.entity.common_mysql.Workspace;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

/**
 * @author huydn on 16/8/24 14:23
 */
@Getter
@Setter
public class CheckTokenResponse {

    private int r;
    private String msg;

    private Workspace workspace;
    //    private WorkspaceAccount workspaceAccount;
    private Account account;

    public static CheckTokenResponse build(JSONObject json) {
        if (json == null) {
            return null;
        }

        CheckTokenResponse res = new CheckTokenResponse();
        res.r = json.getInt("r");
        res.msg = json.getString("msg");

        JSONObject data = json.optJSONObject("data");
        if (data == null) {
            return res;
        }

        //parse JSON
        JSONObject workspaceJson = data.optJSONObject("workspace");
        if (workspaceJson != null) {
            Workspace workspace = new Workspace();
            res.setWorkspace(workspace);

            workspace.setId(workspaceJson.optString("id"));
            workspace.setName(workspaceJson.optString("name"));
            workspace.setDomain(workspaceJson.optString("domain"));
            workspace.setAccountId(workspaceJson.optString("accountId"));
            workspace.setDcId(workspaceJson.optString("dcId"));
            workspace.setMysql(workspaceJson.optString("mysql"));
            workspace.setElasticsearch(workspaceJson.optString("elasticsearch"));
            workspace.setMongodb(workspaceJson.optString("mongodb"));
            workspace.setStatus((byte) workspaceJson.optInt("status"));
            workspace.setSetting(workspaceJson.optString("setting"));
            workspace.setCreated(workspaceJson.optLong("created"));
            workspace.setUpdated(workspaceJson.optLong("updated"));
            workspace.setBackendSettings(workspaceJson.optString("backendSettings"));
            workspace.setDateFormat(workspaceJson.optString("dateFormat"));
            workspace.setTimezone(workspaceJson.optString("timezone"));
            workspace.setReadyForUse(workspaceJson.optBoolean("readyForUse"));
            workspace.setLanguage(workspaceJson.optString("language"));
            workspace.setObjectMapping(workspaceJson.optString("objectMapping"));
            workspace.setNumberFormat(workspaceJson.optString("numberFormat"));
            workspace.setTimeFormat(workspaceJson.optString("timeFormat"));
        }

        JSONObject accountJson = data.optJSONObject("account");
        if (accountJson != null) {
            Account account = new Account();
            res.setAccount(account);

            account.setId(accountJson.optString("id"));
            account.setLastName(accountJson.optString("lastName"));
            account.setFirstName(accountJson.optString("firstName"));
            account.setCreated(accountJson.optLong("created"));
            account.setUpdated(accountJson.optLong("updated"));
            account.setAddress(accountJson.optString("address"));
            account.setDateFormat(accountJson.optString("dateFormat"));
            account.setTimezone(accountJson.optString("timezone"));
            account.setLanguage(accountJson.optString("language"));
            account.setTimeFormat(accountJson.optString("timeFormat"));
            account.setEmail(accountJson.optString("email"));
            account.setNumberFormat(accountJson.optString("numberFormat"));
            account.setSetting(accountJson.optString("setting"));
            account.setLanguage(accountJson.optString("language"));
            account.setAvatar(accountJson.optString("avatar"));
            account.setStatus((byte) accountJson.optInt("status"));
        }

        return res;
    }

}


