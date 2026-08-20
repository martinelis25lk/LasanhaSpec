variable "aws_region" {
  default = "us-east-1"
}

variable "project_name" {
  default = "lasanhaspec"
}

variable "instance_type" {
  default = "t3.micro"
}

variable "my_ip" {
  description = "Seu IP no formato x.x.x.x/32"
  type        = string
  sensitive   = true
}

variable "key_pair_name" {
  default = "lasanhaspec-key"
}