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
