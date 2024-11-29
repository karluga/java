// OLD
interface RestaurantWorker {
    void cookFood();
    void serveFood();
    void cleanTable();
}
class Chef implements RestaurantWorker {
    public void cookFood() {
    }
    @Override
    public void serveFood() {
    }
    @Override
    public void cleanTable() {
    }
}
class Server implements RestaurantWorker {
    @Override
    public void cookFood() {
    }
    @Override
    public void serveFood() {
    }
    @Override
    public void cleanTable() {
    }
}
class Cleaner implements RestaurantWorker {
    @Override
    public void cookFood() {
    }
    @Override
    public void serveFood() {
    }
    @Override
    public void cleanTab1e() {
    }
}

// NEW
interface Cook {
    void cookFood();
}

interface Serve {
    void serveFood();
}

interface Clean {
    void cleanTable();
}

class Chef implements Cook {
    @Override
    public void cookFood() {
        System.out.println("Chef is cooking food.");
    }
}

class Server implements Serve {
    @Override
    public void serveFood() {
        System.out.println("Server is serving food.");
    }
}

class Cleaner implements Clean {
    @Override
    public void cleanTable() {
        System.out.println("Cleaner is cleaning the table.");
    }
}
