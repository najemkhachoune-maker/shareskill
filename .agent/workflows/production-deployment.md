---
description: Plan de déploiement production en 24h
---

# 🚀 Plan de Production SkillVerse (24h)

## ✅ Phase 1 : Corrections Critiques (2h)

### 1.1 Persistance du Chat ✓
```bash
# FAIT - chat-service redémarré avec SecurityConfig corrigé
wsl docker compose restart chat-service
```

### 1.2 Migration PostgreSQL (1h30)
```bash
# 1. Créer docker-compose.prod.yml avec PostgreSQL
# 2. Mettre à jour application.yml de chaque service
# 3. Tester les migrations
wsl docker compose -f docker-compose.prod.yml up -d postgres
```

### 1.3 Variables d'Environnement (30min)
```bash
# Créer .env.production avec :
# - DATABASE_URL
# - JWT_SECRET
# - CORS_ORIGINS
# - API_GATEWAY_URL
```

---

## 🔒 Phase 2 : Sécurité Minimale (3h)

### 2.1 CORS Production (30min)
- Remplacer `localhost` par domaine réel dans gateway-service
- Ajouter validation des origines

### 2.2 Validation JWT (1h30)
- Activer vérification JWT dans chaque microservice
- Configurer Spring Security avec clé publique

### 2.3 Rate Limiting (1h)
- Ajouter Redis pour rate limiting
- Configurer limites par endpoint

---

## 🐳 Phase 3 : Déploiement (4h)

### 3.1 Docker Production (2h)
```bash
# Optimiser Dockerfiles (multi-stage builds)
# Réduire taille des images
wsl docker compose -f docker-compose.prod.yml build --no-cache
```

### 3.2 Nginx SSL (1h30)
```bash
# Installer Certbot
# Générer certificat Let's Encrypt
# Configurer nginx.conf avec SSL
```

### 3.3 Déploiement Cloud (30min)
```bash
# Option 1: Docker Swarm
# Option 2: Kubernetes (si temps)
# Option 3: VPS simple avec docker-compose
```

---

## 📊 Phase 4 : Tests & Monitoring (2h)

### 4.1 Tests E2E (1h)
- Login/Logout
- Recherche par skill
- Chat temps réel
- Persistance messages

### 4.2 Health Checks (30min)
```bash
# Vérifier tous les /health endpoints
curl http://production-url/api/auth/health
curl http://production-url/api/profiles/health
curl http://production-url/chat/health
```

### 4.3 Documentation (30min)
- README.md de déploiement
- Variables d'environnement requises
- Procédure de rollback

---

## 📝 Checklist Finale

- [ ] PostgreSQL configuré et testé
- [ ] Variables d'environnement externalisées
- [ ] CORS configuré pour production
- [ ] JWT validation active
- [ ] Rate limiting en place
- [ ] Images Docker optimisées
- [ ] SSL/HTTPS configuré
- [ ] Tests E2E passés
- [ ] Health checks OK
- [ ] Documentation à jour
- [ ] Backup DB configuré
- [ ] Logs centralisés (optionnel)

---

## 🆘 Rollback Plan

En cas de problème :
```bash
# 1. Revenir à la version précédente
wsl docker compose down
wsl docker compose -f docker-compose.yml up -d

# 2. Restaurer la base de données
pg_restore -d skillverse backup.sql

# 3. Vérifier les services
wsl docker compose ps
```
