# 🚀 SkillVerse - Déploiement Production

## Prérequis
- Docker & Docker Compose installés
- Domaine configuré (DNS pointant vers votre serveur)
- Ports 80, 443, 5432 disponibles

## 📋 Étapes de Déploiement

### 1. Préparation

```bash
# Cloner le repository
git clone https://github.com/votre-repo/skillverse.git
cd skillverse

# Copier le template d'environnement
cp .env.production.template .env.production

# IMPORTANT: Éditer .env.production et changer TOUTES les valeurs
nano .env.production
```

### 2. Configuration SSL (Let's Encrypt)

```bash
# Installer Certbot
sudo apt-get update
sudo apt-get install certbot

# Générer le certificat
sudo certbot certonly --standalone -d yourdomain.com -d www.yourdomain.com

# Copier les certificats
sudo mkdir -p nginx/ssl
sudo cp /etc/letsencrypt/live/yourdomain.com/fullchain.pem nginx/ssl/
sudo cp /etc/letsencrypt/live/yourdomain.com/privkey.pem nginx/ssl/
```

### 3. Build et Démarrage

```bash
# Build les images
docker compose -f docker-compose.prod.yml build --no-cache

# Démarrer PostgreSQL d'abord
docker compose -f docker-compose.prod.yml up -d postgres redis

# Attendre que PostgreSQL soit prêt (30 secondes)
sleep 30

# Démarrer tous les services
docker compose -f docker-compose.prod.yml up -d
```

### 4. Vérification

```bash
# Vérifier que tous les services sont UP
docker compose -f docker-compose.prod.yml ps

# Tester les health checks
curl https://yourdomain.com/api/auth/health
curl https://yourdomain.com/api/profiles/health
curl https://yourdomain.com/chat/health

# Vérifier les logs
docker compose -f docker-compose.prod.yml logs -f gateway-service
```

## 🔄 Mise à Jour

```bash
# Pull les dernières modifications
git pull origin main

# Rebuild et redémarrer
docker compose -f docker-compose.prod.yml up -d --build
```

## 🛑 Arrêt

```bash
# Arrêter tous les services
docker compose -f docker-compose.prod.yml down

# Arrêter ET supprimer les volumes (ATTENTION: perte de données!)
docker compose -f docker-compose.prod.yml down -v
```

## 💾 Backup Base de Données

```bash
# Backup
docker exec skillverse-postgres pg_dump -U skillverse skillverse > backup_$(date +%Y%m%d).sql

# Restore
docker exec -i skillverse-postgres psql -U skillverse skillverse < backup_20231231.sql
```

## 🆘 Troubleshooting

### Service ne démarre pas
```bash
# Voir les logs
docker compose -f docker-compose.prod.yml logs service-name

# Redémarrer un service spécifique
docker compose -f docker-compose.prod.yml restart service-name
```

### Problème de connexion DB
```bash
# Vérifier PostgreSQL
docker exec -it skillverse-postgres psql -U skillverse -d skillverse

# Lister les tables
\dt

# Vérifier les connexions
SELECT * FROM pg_stat_activity;
```

### Certificat SSL expiré
```bash
# Renouveler
sudo certbot renew

# Copier les nouveaux certificats
sudo cp /etc/letsencrypt/live/yourdomain.com/fullchain.pem nginx/ssl/
sudo cp /etc/letsencrypt/live/yourdomain.com/privkey.pem nginx/ssl/

# Redémarrer Nginx
docker compose -f docker-compose.prod.yml restart frontend
```

## 📊 Monitoring

### Logs en temps réel
```bash
# Tous les services
docker compose -f docker-compose.prod.yml logs -f

# Service spécifique
docker compose -f docker-compose.prod.yml logs -f gateway-service
```

### Métriques
```bash
# Utilisation CPU/RAM
docker stats

# Espace disque
df -h
du -sh /var/lib/docker/volumes/
```

## 🔐 Sécurité

### Firewall (UFW)
```bash
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 22/tcp
sudo ufw enable
```

### Rotation des secrets
```bash
# 1. Générer nouveau JWT_SECRET
openssl rand -base64 64

# 2. Mettre à jour .env.production

# 3. Redémarrer auth-service
docker compose -f docker-compose.prod.yml restart auth-service
```

## 📞 Support

En cas de problème, vérifier :
1. Les logs Docker
2. Les variables d'environnement
3. La connectivité réseau
4. L'espace disque disponible
