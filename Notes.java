private HashMap<String, Product> getProductsMapAsync(List<Long> ids) {
        ExecutorService executor = Executors.newFixedThreadPool(ids.size());
        HashMap<String, Product> map = new HashMap<>();
        List<CompletableFuture<Object>> collect = ids.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> {
                    try {
                        Product product = productService.initTokenAndProduct(id).get();
                        map.put(id.toString(), product);
                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }, executor))
                .collect(Collectors.toList());

        CompletableFuture.allOf(collect.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        return map;
    }



private List<Product> getProductsFromService(List<Long> ids) {
        List<CompletableFuture<Product>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        AtomicReference<List<Product>> products = new AtomicReference<>(new ArrayList<>());
        ids.forEach(id -> {
            CompletableFuture<CompletableFuture<Product>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return productService.getProduct(id);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }, executor);
            futures.add(future.thenCompose(f -> f));
        });
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.thenAccept(v -> {
            products.set(futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        }).join();
        return products.get();
    }
