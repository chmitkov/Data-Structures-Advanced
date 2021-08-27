import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class RoyaleArena implements IArena {

    private Map<Integer, Battlecard> cardsByIds;
    private Map<CardType, Set<Battlecard>> cardsByTypes;

    public RoyaleArena() {
        cardsByIds = new LinkedHashMap<>();
        cardsByTypes = new HashMap<>();
    }

    @Override
    public void add(Battlecard card) {
        cardsByIds.putIfAbsent(card.getId(), card);
        cardsByTypes.putIfAbsent(card.getType(), new TreeSet<>(Battlecard::compareTo));
        cardsByTypes.get(card.getType()).add(card);
    }

    @Override
    public boolean contains(Battlecard card) {
        return cardsByIds.containsKey(card.getId());
    }

    @Override
    public int count() {
        return cardsByIds.size();
    }

    @Override
    public void changeCardType(int id, CardType type) {
        Battlecard battlecard = cardsByIds.get(id);
        if (battlecard == null) {
            throw new IllegalArgumentException();
        }
        battlecard.setType(type);
    }

    @Override
    public Battlecard getById(int id) {
        Battlecard battlecard = cardsByIds.get(id);

        if (battlecard == null) {
            throw new UnsupportedOperationException();
        }
        return battlecard;
    }

    @Override
    public void removeById(int id) {
        Battlecard battlecard = cardsByIds.remove(id);
        if (battlecard == null) {
            throw new UnsupportedOperationException();
        }

        cardsByTypes.get(battlecard.getType()).remove(battlecard);
    }

    @Override
    public Iterable<Battlecard> getByCardType(CardType type) {
        return getBattlecardsByType(type);
    }

    @Override
    public Iterable<Battlecard> getByTypeAndDamageRangeOrderedByDamageThenById(CardType type, int lo, int hi) {
        return getBattlecardsByType(type).stream()
                .filter(card -> card.getDamage() > lo && card.getDamage() < hi)
                .sorted(Battlecard::compareTo)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Battlecard> getByCardTypeAndMaximumDamage(CardType type, double damage) {
        List<Battlecard> result = getBattlecardsByType(type)
                .stream()
                .filter(card -> card.getDamage() <= damage)
                .sorted(Battlecard::compareTo)
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new UnsupportedOperationException();
        }

        return result;
    }

    @Override
    public Iterable<Battlecard> getByNameOrderedBySwagDescending(String name) {
        List<Battlecard> result = getBattlecardsByPredicate(c -> c.getName().equals(name));

        if (result.isEmpty()) {
            throw new UnsupportedOperationException();
        }

        return result.stream()
                .sorted(Comparator.comparing(Battlecard::getSwag).reversed()
                        .thenComparing(Battlecard::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Battlecard> getByNameAndSwagRange(String name, double lo, double hi) {
        List<Battlecard> result = getBattlecardsByPredicate(c -> c.getName().equals(name) &&
                c.getSwag() >= lo && c.getSwag() < hi);

        if (result.isEmpty()) {
            throw new UnsupportedOperationException();
        }

        return result
                .stream()
                .sorted(Comparator.comparing(Battlecard::getSwag).reversed()
                        .thenComparing(Battlecard::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Battlecard> getAllByNameAndSwag() {
        Map<String, Battlecard> battlecards = new LinkedHashMap<>();

        for (Battlecard battlecard : cardsByIds.values()) {
            if (!battlecards.containsKey(battlecard.getName())) {
                battlecards.put(battlecard.getName(), battlecard);
            } else {
                if (battlecards.get(battlecard.getName()).getSwag() < battlecard.getSwag()) {
                    battlecards.put(battlecard.getName(), battlecard);
                }
            }
        }

        return battlecards.values();
    }

    @Override
    public Iterable<Battlecard> findFirstLeastSwag(int n) {
        if (n > count()) {
            throw new UnsupportedOperationException();
        }

        return cardsByIds.values()
                .stream()
                .sorted(Comparator.comparing(Battlecard::getSwag)
                        .thenComparing(Battlecard::getId))
                .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Battlecard> getAllInSwagRange(double lo, double hi) {
        return getBattlecardsByPredicate(c -> c.getSwag() >= lo && c.getSwag() <= hi)
                .stream()
                .sorted(Comparator.comparing(Battlecard::getSwag))
                .collect(Collectors.toList());
    }

    @Override
    public Iterator<Battlecard> iterator() {
        return cardsByIds.values().iterator();
    }

    private List<Battlecard> getBattlecardsByPredicate(Predicate<Battlecard> predicate) {
        List<Battlecard> battlecards = new ArrayList<>();

        for (Battlecard battlecard : cardsByIds.values()) {
            if (predicate.test(battlecard)) {
                battlecards.add(battlecard);
            }
        }

        return battlecards;
    }

    private Set<Battlecard> getBattlecardsByType(CardType type) {
        Set<Battlecard> battlecards = cardsByTypes.get(type);

        if (battlecards == null || battlecards.isEmpty()) {
            throw new UnsupportedOperationException();
        }

        return battlecards;
    }
}
