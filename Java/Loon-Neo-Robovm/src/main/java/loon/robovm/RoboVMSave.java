/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.robovm;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import loon.Save;
import loon.SaveBatchImpl;

import org.robovm.apple.foundation.NSFileManager;
import org.robovm.apple.foundation.NSSearchPathDirectory;
import org.robovm.apple.foundation.NSSearchPathDomainMask;

public class RoboVMSave implements Save {

  private final RoboVMGame game;
  private final Connection conn;

  public RoboVMSave(RoboVMGame game) {
    this.game = game;

    String dbDir = null;
    try {
      try {
        Class.forName("SQLite.JDBCDriver");
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }

      dbDir = NSFileManager.getDefaultManager().getURLsForDirectory(
        NSSearchPathDirectory.DocumentDirectory,
        NSSearchPathDomainMask.UserDomainMask).get(0).getPath();
      File dbFile = new File(dbDir, game.config.storageFileName);
      dbFile.getParentFile().mkdirs();

      game.log().info("Using db in file: " + dbFile.getAbsolutePath());
      conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

      try (Statement stmt = conn.createStatement()) {
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " +
          "Data (DataKey ntext PRIMARY KEY, DataValue ntext NOT NULL)");
      }

    } catch (SQLException sqe) {
      throw new RuntimeException("Failed to initialize storage [dbDir=" + dbDir + "]", sqe);
    }
  }

  @Override
  public boolean isPersisted() {
    return true;
  }

  @Override
  public Iterable<String> keys() {
    try {
      List<String> keys = new ArrayList<String>();
      try (Statement stmt = conn.createStatement()) {
        ResultSet rs = stmt.executeQuery("select DataKey from Data");
        while (rs.next()) {
          keys.add(rs.getString(1));
        }
      }
      return keys;

    } catch (SQLException sqe) {
      throw new RuntimeException("keys() failed", sqe);
    }
  }

  @Override
  public String getItem(String key) {
    try {
      String sql = "select DataValue from Data where DataKey = ?";
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, key);
        ResultSet rs = stmt.executeQuery();
        String result = null;
        while (rs.next()) {
          result = rs.getString(1);
        }
        return result;
      }

    } catch (SQLException sqe) {
      throw new RuntimeException("getItem(" + key + ") failed", sqe);
    }
  }

  @Override
  public void setItem(String key, String value) throws RuntimeException {
    try {
      String usql = "update Data set DataValue = ? where DataKey = ?";
      try (PreparedStatement ustmt = conn.prepareStatement(usql)) {
        ustmt.setString(1, value);
        ustmt.setString(2, key);
        if (ustmt.executeUpdate() > 0) return;
      }

      String isql = "insert into Data (DataKey, DataValue) values (?, ?)";
      try (PreparedStatement istmt = conn.prepareStatement(isql)) {
        istmt.setString(1, key);
        istmt.setString(2, value);
        if (istmt.executeUpdate() == 0) {
          game.log().warn("Failed to insert storage item [key=" + key + "]");
        }
      }

    } catch (SQLException sqe) {
      throw new RuntimeException("setItem(" + key + ", " + value + ") failed", sqe);
    }
  }

  @Override
  public void removeItem(String key) {
    try {
      String sql = "delete from Data where DataKey = ?";
      try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, key);
        stmt.executeUpdate();
      }
    } catch (SQLException sqe) {
      throw new RuntimeException("removeItem(" + key + ") failed", sqe);
    }
  }

  @Override
  public Batch startBatch() {
    return new SaveBatchImpl(this);
  }
}
