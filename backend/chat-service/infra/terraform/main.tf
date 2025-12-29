terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "eu-west-1"
}

# -------------------------------
# VPC
# -------------------------------
module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 5.0"

  name = "challenge-vpc"
  cidr = "10.0.0.0/16"

  azs             = ["eu-west-1a", "eu-west-1b", "eu-west-1c"]
  private_subnets = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  public_subnets  = ["10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"]

  enable_nat_gateway = true
  single_nat_gateway = true
}

# -------------------------------
# Security Group
# -------------------------------
resource "aws_security_group" "challenge_sg" {
  name        = "challenge-sg"
  description = "Allow HTTP/HTTPS inbound"
  vpc_id      = module.vpc.vpc_id

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "All outbound"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# -------------------------------
# IAM Role for Nodes
# -------------------------------
resource "aws_iam_role" "challenge_node_role" {
  name = "challenge-node-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action    = "sts:AssumeRole"
      Effect    = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
    }]
  })
}

# Attach AmazonEKSFullAccess
resource "aws_iam_role_policy_attachment" "eks_full_access" {
  role       = aws_iam_role.challenge_node_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSFullAccess"
}

# Attach IAMFullAccess
resource "aws_iam_role_policy_attachment" "iam_full_access" {
  role       = aws_iam_role.challenge_node_role.name
  policy_arn = "arn:aws:iam::aws:policy/IAMFullAccess"
}

# Attach AmazonEC2FullAccess
resource "aws_iam_role_policy_attachment" "ec2_full_access" {
  role       = aws_iam_role.challenge_node_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2FullAccess"
}

# -------------------------------
# EKS Cluster
# -------------------------------
module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 20.0"

  cluster_name    = "challenge-eks"
  cluster_version = "1.29"
  vpc_id          = module.vpc.vpc_id
  subnet_ids      = module.vpc.private_subnets

  eks_managed_node_groups = {
    default = {
      instance_types = ["t3.medium"]
      min_size       = 2
      desired_size   = 2
      max_size       = 3
      additional_security_group_ids = [aws_security_group.challenge_sg.id]
      iam_role_arn                  = aws_iam_role.challenge_node_role.arn
    }
  }
}

# -------------------------------
# Outputs
# -------------------------------
output "cluster_name" {
  value = module.eks.cluster_name
}

output "cluster_endpoint" {
  value = module.eks.cluster_endpoint
}

output "cluster_certificate_authority_data" {
  value = module.eks.cluster_certificate_authority_data
}

output "security_group_id" {
  value = aws_security_group.challenge_sg.id
}

output "node_role_arn" {
  value = aws_iam_role.challenge_node_role.arn
}

