package сontrol;


import account.Account;

public class Client {
    private String name;
    private Account account;

    public Account getAccount() {
        return account;
    }
    public Client(Account account, String name) {
        this.name = name;
        this.account = account;
    }
    public int viewBalance(int numberCard) {
        return account.getCards().get(numberCard).viewBalance();
    }
    public void blockCard(int numberCard) {
        account.getCards().get(numberCard).block();
    }
    public void deposit(int numberCard, int sum) {
        account.getCards().get(numberCard).deposit(sum);
    }
    public void payment(int numberCard, int sum) throws Exception {
        account.getCards().get(numberCard).payment(sum);
    }
}
