package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class dbUtils {

    public static SQLiteDataSource dbInit(String filename) {
        String url = "jdbc:sqlite:./%s".formatted(filename);
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {

            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                        "number TEXT NOT NULL," +
                                        "pin TEXT NOT NULL," +
                                        "balance INTEGER DEFAULT 0)");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return dataSource;
    }

    public static Account addAccount(String cardNumber, String pinCode) {
        try (Connection con = Main.dbDataSource.getConnection()) {

            try (PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO card (number, pin) " +
                                                                            "VALUES (?, ?)")) {
                preparedStatement.setString(1, cardNumber);
                preparedStatement.setString(2, pinCode);
                try {
                    preparedStatement.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return getAccount(cardNumber, pinCode);
    }

    public static Account getAccount(Account account) {
        return getAccount(account.cardNumber());
    }

    public static Account getAccount(String inputCard, String inputPIN) {
        Account account = getAccount(inputCard);
        assert account != null;
        if (account.pinCode().equals(inputPIN)) {
            return account;
        }
        return null;
    }

    public static boolean deleteAccount(Account account) {
        try (Connection con = Main.dbDataSource.getConnection()) {
            try (PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM card " +
                                                                            "WHERE id = ?")) {
                preparedStatement.setInt(1, account.ID());
                try {
                    return preparedStatement.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param account The account to add balance to
     * @param input the amount of balance to add
     * @return the updated account
     */
    public static Account addBalance(Account account, int input) {
        try (Connection con = Main.dbDataSource.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE card " +
                                                             "SET balance = ? " +
                                                             "WHERE id = ?;")) {
                int sum = account.balance() + input;
                ps.setInt(1, sum);
                ps.setInt(2, account.ID());

                try {
                    ps.execute();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return getAccount(account);
    }

    public static Account getAccount(String cardNumber) {
        try (Connection con = Main.dbDataSource.getConnection()) {
            // Statement creation
            try (PreparedStatement statement = con.prepareStatement("SELECT * FROM card " +
                                                                    "WHERE number = ?;")) {
                statement.setString(1, cardNumber);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int id = resultSet.getInt("ID");
                        String resultCard = resultSet.getString("number");
                        String resultPin = resultSet.getString("pin");
                        int balance = resultSet.getInt("balance");


                        return new Account(id, resultCard, resultPin, balance);

                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
