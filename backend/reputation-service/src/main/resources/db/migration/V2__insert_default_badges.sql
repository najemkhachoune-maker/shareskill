INSERT INTO badges (id, name, description, criteria, created_at) VALUES
('3d0dbd18-c995-491f-9732-157052d1375b', 'First Review', 'Awarded after receiving the first review', '{"type":"review_count","side":"received","minReviews":1}', now()),
('500b1d8d-1d11-4a0b-a7ed-843045ca94f3', 'Top Contributor', 'Received at least 10 reviews', '{"type":"review_count","side":"received","minReviews":10}', now()),
('37285170-596a-4e0e-b0a5-008cf0e1e4c1', 'Top Rated', 'Average rating >= 4.5 for at least 3 received reviews', '{"type":"avg_rating","side":"received","minReviews":3,"minAvgRating":4.5}', now()),
('e4c30007-d2bc-4827-aeb6-646bef296e7a', 'Clean Reviewer', 'No flagged reviews among received reviews', '{"type":"flags","side":"received","minReviews":2,"maxFlags":0}', now()),
('aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee', 'Highly Rated User', 'Average rating ≥ 4.8 for at least 5 received reviews', '{"type":"avg_rating","side":"received","minReviews":5,"minAvgRating":4.8}', now());